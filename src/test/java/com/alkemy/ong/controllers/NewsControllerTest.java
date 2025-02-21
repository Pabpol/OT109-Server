package com.alkemy.ong.controllers;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import com.alkemy.ong.dtos.requests.NewPostPutRequestDTO;
import com.alkemy.ong.dtos.responses.CategoryDTO;
import com.alkemy.ong.dtos.responses.NewDTO;
import com.alkemy.ong.entities.Category;
import com.alkemy.ong.exceptions.NotFoundException;
import com.alkemy.ong.repositories.NewsRepository;
import com.alkemy.ong.services.INewsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private INewsService newsService;

        @MockBean
        private NewsRepository newsRepository;

        @Autowired
        private JacksonTester<NewPostPutRequestDTO> jsonNewsPostPutDTO;
        @Autowired
        private JacksonTester<NewDTO> jsonNewsDTO;

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetrieveByIdWhenExistsWithAdminRole() throws Exception {

                // given

                given(newsService.getById(1L)).willReturn(new NewDTO(1L, "name", "content", "image", new CategoryDTO()));

                // when
                MockHttpServletResponse response = mockMvc.perform(get("/news/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.getContentAsString()).isEqualTo(jsonNewsDTO
                                .write(new NewDTO(1L, "name", "content", "image", new CategoryDTO())).getJson());
        }

        @Test
        @WithUserDetails(value = "tamaraceballos@gmail.com")
        void canRetrieveByIdWhenExistsWithUserRole() throws Exception {

                // given

                given(newsService.getById(1L)).willReturn(new NewDTO(1L, "name", "content", "image", new CategoryDTO()));

                // when
                MockHttpServletResponse response = mockMvc.perform(get("/news/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.getContentAsString()).isEqualTo(jsonNewsDTO
                                .write(new NewDTO(1L, "name", "content", "image", new CategoryDTO())).getJson());
        }

        @Test
        void canRetrieveByIdWhenExistsWithoutRole() throws Exception {

                // given

                given(newsService.getById(1L)).willReturn(new NewDTO(1L, "name", "content", "image", new CategoryDTO()));

                // when
                MockHttpServletResponse response = mockMvc.perform(get("/news/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetrieveByIdWhenDoesNotExists() throws Exception {

                given(newsService.getById(1L)).willThrow(new NotFoundException(""));

                // when
                MockHttpServletResponse response = mockMvc.perform(get("/news/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                assertThat(response.getContentAsString()).isEmpty();
        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canCreateNewsWithAdminRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(post("/news")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        }

        @Test
        @WithUserDetails(value = "tamaraceballos@gmail.com")
        void canCreateNewsWithUserRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(post("/news")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        }

        @Test
        void createNewsWithoutRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(post("/news")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetreiveWhenCreateNewsThrowBadRequest() throws Exception {
                // when
                MockHttpServletResponse response = mockMvc.perform(post("/news")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO(null, "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetreiveWhenCreateNewsThrowNotFound() throws Exception {
                // given

                // given(newsService.saveNews(null)).willThrow(new NotFoundException(""));
                when(newsService.saveNews(any())).thenThrow(NotFoundException.class);

                // when
                MockHttpServletResponse response = mockMvc.perform(post("/news")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canUpdateNewsWithAdminRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(put("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        }

        @Test
        @WithUserDetails(value = "tamaraceballos@gmail.com")
        void canUpdateNewsWithUserRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(put("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        }

        @Test
        void updateNewsWithoutRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(put("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetreiveWhenUpdateNewsThrowBadRequest() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(put("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO(null, "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetreiveWhenUpdateNewsThrowNotFound() throws Exception {

                when(newsService.updateNews(any(), any())).thenThrow(NotFoundException.class);

                // when
                MockHttpServletResponse response = mockMvc.perform(put("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson())
                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canDeleteNewsWithAdminRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(delete("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        }

        @Test
        @WithUserDetails(value = "tamaraceballos@gmail.com")
        void canDeleteNewsWithUserRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(delete("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        }

        @Test
        void deleteNewsWithoutRole() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(delete("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        }

        @Test
        @WithUserDetails(value = "admin@admin.com")
        void canRetreiveWhenDeleteNewsThrowNotFound() throws Exception {

                // given
                doThrow(NotFoundException.class).when(newsService).delete(any());
                // when
                MockHttpServletResponse response = mockMvc.perform(delete("/news/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonNewsPostPutDTO
                                                .write(new NewPostPutRequestDTO("name", "content", "image", 1L))
                                                .getJson()))
                                .andReturn().getResponse();

                // then
                assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        }

}
