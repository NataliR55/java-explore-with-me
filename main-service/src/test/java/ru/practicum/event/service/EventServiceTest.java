package ru.practicum.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;
    //    @Mock
//    private StatsService statsService;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private final Pageable pageable = PageRequest.of(0, 100);

    private Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
    private LocationDto locationDto = LocationMapper.toLocationDto(location);
    private Category category = Category.builder().id(1L).name("Cinema").build();
    private CategoryDto categoryDto = CategoryMapper.toCategoryDto(category);
    private User user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    private UserDto userDto = UserMapper.toUserDto(user);

    private final Event event1 = Event.builder()
            .id(1L)
            .title("title1")
            .annotation("annotation1-----------------------------annotation1")
            .description("description1---------------------------description1")
            .eventDate(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
            .category(category)
            .location(location)
            .paid(false)
            .participantLimit(0)
            .requestModeration(false)
            .initiator(user)
            .state(EventState.PENDING)
            .createdOn(LocalDateTime.now().minusHours(3).truncatedTo(ChronoUnit.SECONDS))
            .publishedOn(null)
            .build();


    @Nested
    class CreateEvent {
        @BeforeEach
        public void beforeEach() {
            newEventDto = NewEventDto.builder()
                    .title(event1.getTitle())
                    .annotation(event1.getAnnotation())
                    .description(event1.getDescription())
                    .eventDate(event1.getEventDate())
                    .category(event1.getCategory().getId())
                    .location(locationDto)
                    .paid(event1.getPaid())
                    .participantLimit(event1.getParticipantLimit())
                    .requestModeration(event1.getRequestModeration())
                    .build();
        }

        @Test
        public void shouldCreate() {
            when(userService.getUserById(event1.getInitiator().getId())).thenReturn(event1.getInitiator());
            when(categoryService.getCategoryById(newEventDto.getCategory())).thenReturn(category);
            when(locationMapper.toLocation(any())).thenCallRealMethod();
            when(locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon()))
                    .thenReturn(Optional.empty());
            when(locationRepository.save(any())).thenReturn(location);
            when(eventMapper.toEvent(any(), any(), any(), any(), any(), any())).thenReturn(event1);
            when(eventRepository.save(any())).thenReturn(event1);
            when(eventMapper.toEventFullDto(any(), any(), any())).thenReturn(eventFullDto1);

            EventFullDto eventFullDtoFromRepository = eventService.createEventByPrivate(event1.getInitiator().getId(), newEventDto);

            assertEquals(eventFullDto1, eventFullDtoFromRepository);

            verify(userService, times(1)).getUserById(any());
            verify(categoryService, times(1)).getCategoryById(any());
            verify(locationMapper, times(1)).toLocation(any());
            verify(locationRepository, times(1)).findByLatAndLon(any(), any());
            verify(locationRepository, times(1)).save(any());
            verify(eventMapper, times(1)).toEvent(any(), any(), any(), any(), any(), any());
            verify(eventRepository, times(1)).save(eventArgumentCaptor.capture());
            verify(eventMapper, times(1)).toEventFullDto(any(), any(), any());

            Event savedEvent = eventArgumentCaptor.getValue();

            checkResults(event1, savedEvent);
        }
    }


}

