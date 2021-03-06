package tripmaster.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tripmaster.common.attraction.AttractionData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.user.User;
import tripmaster.gps.GpsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GpsServiceTest {

	@MockBean GpsUtil gpsUtil;
	@Autowired GpsService gpsService;
	
	private final static double LATITUDE_GPS_ONE = 0.21;
	private final static double LONGITUDE_GPS_ONE = -0.00022;
	
	@Test
	public void givenUser_whenGetCurrentUserLocation_thenReturnsCurrentLocation() {
		// GIVEN mock GpsUtil
		User user = mockGetUserLocation(1);
		// WHEN
		VisitedLocationData resultLocation = gpsService.getCurrentUserLocation(user.userId.toString());
		// THEN
		assertNotNull(resultLocation);
		assertTrue(resultLocation.userId.equals(user.userId));
		assertEquals(LATITUDE_GPS_ONE, resultLocation.location.latitude, 0.0000000001);
		assertEquals(LONGITUDE_GPS_ONE, resultLocation.location.longitude, 0.0000000001);
	}

	@Test
	public void givenUserList_whenTrackAllUserLocations_thenAddsVisitedLocationToAllUsers() {
		// GIVEN mock getAllUsers
		List<User> givenUsers = new ArrayList<User>();
		for (int i=0; i<5; i++) {
			givenUsers.add(mockGetUserLocation(i));
		}
		// WHEN
		gpsService.trackAllUserLocations(givenUsers);
		// THEN
		for (User user : givenUsers) {
			assertNotNull(user);
			assertNotNull(user.getVisitedLocations());
			assertEquals(1, user.getVisitedLocations().size());
		}
	}
	
	@Test
	public void givenMock_getAllAttractions_thenReturnsFullList() {
		// GIVEN
		List<Attraction> givenAttractions = new ArrayList<Attraction>();
		Attraction attraction1 = new Attraction("name1", "", "", 1, 0);
		Attraction attraction2 = new Attraction("name2", "", "", 0, 0);
		givenAttractions.add(attraction1);
		givenAttractions.add(attraction2);
		when(gpsUtil.getAttractions()).thenReturn(givenAttractions);
		// WHEN
		List<AttractionData> resultAttractions = gpsService.getAllAttractions();
		// THEN
		assertEquals(2, resultAttractions.size());
		assertEquals(attraction1.attractionName, resultAttractions.get(0).name);
		assertEquals(attraction2.attractionName, resultAttractions.get(1).name);
	}
	
	private User mockGetUserLocation(int index) {
		User user = new User(new UUID(11*index,12*index), "name"+index, "phone"+index, "email"+index);
		Location location = new Location(LATITUDE_GPS_ONE*index,LONGITUDE_GPS_ONE*index);
		VisitedLocation visitedLocation = new VisitedLocation(user.userId, location, new Date(index));
		when(gpsUtil.getUserLocation(user.userId)).thenReturn(visitedLocation);
		return user;
	}
}