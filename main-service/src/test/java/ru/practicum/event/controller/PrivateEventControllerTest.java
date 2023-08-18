package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.service.RequestService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateEventController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class PrivateEventControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private RequestService requestService;

    private Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
    private LocationDto locationDto = LocationMapper.toLocationDto(location);
    private Category category = Category.builder().id(1L).name("Театр").build();
    private CategoryDto categoryDto = CategoryMapper.toCategoryDto(category);
    private User user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    private UserDto userDto = UserMapper.toUserDto(user);
    private Event event;
    private EventFullDto eventFullDto;
    private NewEventDto newEventDto;
    @Nested
    class CreateEventPrivate {
        @BeforeEach
        public void beforeEach() {
            newEventDto = NewEventDto.builder()
                    .annotation("annotation---------------------annotation")
                    .title("title1")
                    .category(1L)
                    .description("description-------------------description")
                    .eventDate(LocalDateTime.now().plusHours(10).truncatedTo(ChronoUnit.SECONDS))
                    .location(locationDto)
                    .paid(true)
                    .participantLimit(0)
                    .requestModeration(false)
                    .build();
            event = EventMapper.fromNewEventDtoToEvent(newEventDto, category, user, location);
            eventFullDto=EventMapper.toEventFullDto(event);
        }

        @Test
        public void shouldCreate() throws Exception {
            when(eventService.createEventPrivate(any(), any())).thenReturn(eventFullDto);
          mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto)));
            verify(eventService).createEventPrivate(any(), any());
        }
    }
}
