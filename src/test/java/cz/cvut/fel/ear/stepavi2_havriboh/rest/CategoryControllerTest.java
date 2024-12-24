package cz.cvut.fel.ear.stepavi2_havriboh.rest;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.rest.CategoryController;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CategoryControllerTest extends BaseControllerTestRunner {

    private CategoryService categoryService;
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryService = Mockito.mock(CategoryService.class);
        categoryController = new CategoryController(categoryService);
        super.setUp(categoryController);
    }

    @Test
    void getAllCategories_shouldReturnCategories() throws Exception {

    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {

    }

    @Test
    void getCategoryById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new CategoryNotFoundException("")).when(categoryService).getCategoryById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Category not found\""));
    }

    @Test
    void createCategory_shouldCreateAndReturn201() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Category1\",\"description\":\"Description1\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$").value("Category created"));

        verify(categoryService, times(1))
                .createCategory("Category1", "Description1");
    }

    @Test
    void updateCategory_shouldUpdateAndReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/rest/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Category1\",\"description\":\"Description1\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").value("Category updated"));

        verify(categoryService, times(1))
                .updateCategoryById(1, "Category1", "Description1");
    }

    @Test
    void deleteCategory_shouldDeleteAndReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").value("Category deleted"));


        verify(categoryService, times(1)).deleteCategoryById(1);
    }

    @Test
    void deleteCategory_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new CategoryNotFoundException("")).when(categoryService).deleteCategoryById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("\"Category not found\""));
    }
}
