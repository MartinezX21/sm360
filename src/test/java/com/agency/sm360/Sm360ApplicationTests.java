package com.agency.sm360;

import static org.assertj.core.api.Assertions.assertThat;

import com.agency.sm360.dao.DealerDAO;
import com.agency.sm360.dao.ListingDAO;
import com.agency.sm360.dto.AmendListingRequest;
import com.agency.sm360.dto.CreateListingRequest;
import com.agency.sm360.dto.DealerDTO;
import com.agency.sm360.entities.Dealer;
import com.agency.sm360.entities.Listing;
import com.agency.sm360.utils.ListingState;
import com.agency.sm360.utils.TierLimitStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
		locations = "classpath:application-test.properties")
class Sm360ApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ListingDAO listingDAO;
	@Autowired
	private DealerDAO dealerDAO;

	@BeforeEach
	void clearAll(){
		listingDAO.deleteAll();
		dealerDAO.deleteAll();
	}

	@Test
	public void whenCreateListing_thenReturnSavedListing() throws Exception{
		// init
		DealerDTO dealerDTO = DealerDTO.builder()
				.name("John Doe")
				.build();
		CreateListingRequest listingRequest = CreateListingRequest.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealerDTO)
				.build();

		// when
		ResultActions response = mockMvc.perform(post("/listings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(listingRequest)));

		// then
		response.andDo(print()).
				andExpect(status().isCreated())
				.andExpect(jsonPath("$.state",
						is(ListingState.draft.name())))
				.andExpect(jsonPath("$.vehicle",
						is(listingRequest.getVehicle())))
				.andExpect(jsonPath("$.dealer.name",
						is(dealerDTO.getName())));

	}

	@Test
	public void givenACreatedListing_whenUpdateListing_thenReturnSavedListing() throws Exception{
		// init
		Dealer dealer = Dealer.builder()
				.name("John Doe")
				.build();
		Listing listing = Listing.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealerDAO.save(dealer))
				.createdAt(new Date())
				.state(ListingState.draft)
				.build();
		listing = listingDAO.save(listing);
		AmendListingRequest amendListingRequest = AmendListingRequest.builder()
				.vehicle("Toyota")
				.price(listing.getPrice())
				.build();

		// when
		ResultActions response = mockMvc.perform(put("/listings/{id}", listing.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(amendListingRequest)));

		// then
		response.andDo(print()).
				andExpect(status().isOk())
				.andExpect(jsonPath("$.vehicle",
						is(amendListingRequest.getVehicle())));
	}

	@Test
	public void whenGetListings_thenReturnMatchingListings() throws Exception{
		// init
		Date now = new Date();
		Dealer dealer = Dealer.builder()
				.name("John Doe")
				.build();
		dealer = dealerDAO.save(dealer);
		Listing listing1 = Listing.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealer)
				.createdAt(now)
				.state(ListingState.draft)
				.build();
		Listing listing2 = Listing.builder()
				.vehicle("Toyota")
				.price(11200d)
				.dealer(dealer)
				.createdAt(now)
				.state(ListingState.published)
				.build();
		Listing listing3 = Listing.builder()
				.vehicle("Mercedes Benz")
				.price(31500d)
				.dealer(dealer)
				.createdAt(now)
				.state(ListingState.draft)
				.build();
		listingDAO.saveAll(Arrays.asList(listing1, listing2, listing3));

		// when
		ResultActions response = mockMvc.perform(get("/listings")
				.contentType(MediaType.APPLICATION_JSON));
		// then
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()", is(3)));

		// when
		response = mockMvc.perform(get("/listings?state="+ListingState.draft.name())
				.contentType(MediaType.APPLICATION_JSON));
		// then
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()", is(2)));

		// when
		response = mockMvc.perform(get("/listings?state="+ListingState.published.name())
				.contentType(MediaType.APPLICATION_JSON));
		// then
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()", is(1)));
	}

	@Test
	public void whenPublishListing_thenReturnPublishedListing() throws Exception{
		// init
		Dealer dealer = Dealer.builder()
				.name("John Doe")
				.build();
		Listing listing = Listing.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealerDAO.save(dealer))
				.createdAt(new Date())
				.state(ListingState.draft)
				.build();
		listing = listingDAO.save(listing);

		// when
		ResultActions response = mockMvc.perform(put("/listings/{id}/publish", listing.getId().toString())
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print()).
				andExpect(status().isOk())
				.andExpect(jsonPath("$.state",
						is(ListingState.published.name())));
	}

	@Test
	public void whenPublishListing_thenUnPublishOldestListing() throws Exception{
		// init
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Dealer dealer = Dealer.builder()
				.name("John Doe")
				.build();
		dealer = dealerDAO.save(dealer);
		Listing listing1 = Listing.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealer)
				.createdAt(format.parse("29/09/2022 16:43"))
				.state(ListingState.published)
				.build();
		listing1 = listingDAO.save(listing1);
		Listing listing2 = Listing.builder()
				.vehicle("Toyota")
				.price(23500d)
				.dealer(dealer)
				.createdAt(format.parse("06/03/2023 10:15"))
				.state(ListingState.published)
				.build();
		listing2 = listingDAO.save(listing2);
		Listing listing3 = Listing.builder()
				.id(UUID.randomUUID())
				.vehicle("Mercedes Benz")
				.price(35150d)
				.dealer(dealer)
				.createdAt(new Date())
				.state(ListingState.draft)
				.build();
		listing3 = listingDAO.save(listing3);

		// when
		ResultActions response = mockMvc.perform(put("/listings/{id}/publish?strategy="+ TierLimitStrategy.unpublish_oldest.name(), listing3.getId().toString())
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print()).
				andExpect(status().isOk())
				.andExpect(jsonPath("$.state",
						is(ListingState.published.name())));
		Listing oldestPublished = listingDAO.findById(listing1.getId()).orElse(null);
		assertThat(oldestPublished).isNotNull();
		assertThat(oldestPublished.getState()).isEqualTo(ListingState.draft);
	}

	@Test
	public void whenPublishListing_thenReturn422() throws Exception{
		// init
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Dealer dealer = Dealer.builder()
				.name("John Doe")
				.build();
		dealer = dealerDAO.save(dealer);
		Listing listing1 = Listing.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealer)
				.createdAt(format.parse("29/09/2022 16:43"))
				.state(ListingState.published)
				.build();
		listing1 = listingDAO.save(listing1);
		Listing listing2 = Listing.builder()
				.vehicle("Toyota")
				.price(23500d)
				.dealer(dealer)
				.createdAt(format.parse("06/03/2023 10:15"))
				.state(ListingState.published)
				.build();
		listing2 = listingDAO.save(listing2);
		Listing listing3 = Listing.builder()
				.id(UUID.randomUUID())
				.vehicle("Mercedes Benz")
				.price(35150d)
				.dealer(dealer)
				.createdAt(new Date())
				.state(ListingState.draft)
				.build();
		listing3 = listingDAO.save(listing3);

		// when
		ResultActions response = mockMvc.perform(put("/listings/{id}/publish", listing3.getId().toString())
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print()).
				andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void whenUnPublishListing_thenReturnUnPublishedListing() throws Exception{
		// init
		Dealer dealer = Dealer.builder()
				.name("John Doe")
				.build();
		Listing listing = Listing.builder()
				.vehicle("Ford")
				.price(15500d)
				.dealer(dealerDAO.save(dealer))
				.createdAt(new Date())
				.state(ListingState.published)
				.build();
		listing = listingDAO.save(listing);

		// when
		ResultActions response = mockMvc.perform(put("/listings/{id}/unpublish", listing.getId().toString())
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print()).
				andExpect(status().isOk())
				.andExpect(jsonPath("$.state",
						is(ListingState.draft.name())));
	}
}
