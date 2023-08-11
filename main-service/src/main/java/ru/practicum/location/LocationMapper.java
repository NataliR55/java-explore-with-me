package ru.practicum.location;

import ru.practicum.location.Location;
import ru.practicum.location.LocationDto;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder().lon(location.getLon()).lat(location.getLat()).build();
    }

    private LocationMapper() {
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder().lon(locationDto.getLon()).lat(locationDto.getLat()).build();
    }

}
