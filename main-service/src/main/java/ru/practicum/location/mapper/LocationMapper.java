package ru.practicum.location.mapper;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

public class LocationMapper {
    private LocationMapper() {
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder().lon(location.getLon()).lat(location.getLat()).build();
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder().lon(locationDto.getLon()).lat(locationDto.getLat()).build();
    }

}
