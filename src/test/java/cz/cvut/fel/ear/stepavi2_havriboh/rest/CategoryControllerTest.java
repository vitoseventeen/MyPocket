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

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest extends BaseControllerTestRunner {

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = Mockito.mock(CategoryService.class);
        CategoryController categoryController = new CategoryController(categoryService);
        super.setUp(categoryController);
    }

    @Test
    void getAllCategories_shouldReturnCategories() throws Exception {
        Category category1 = new Category();
        category1.setName("Category1");
        category1.setDescription("Description1");

        Category category2 = new Category();
        category2.setName("Category2");
        category2.setDescription("Description2");

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Category1"))
                .andExpect(jsonPath("$[1].name").value("Category2"));
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category category = new Category();
        category.setName("Category1");
        category.setDescription("Description1");

        when(categoryService.getCategoryById(1)).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Category1"))
                .andExpect(jsonPath("$.description").value("Description1"));
    }

    @Test
    void getCategoryById_shouldReturn404WhenNotFound() throws Exception {
        when(categoryService.getCategoryById(1)).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Category not found\""));
    }

    @Test
    void updateCategory_shouldReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/rest/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedCategory\",\"description\":\"UpdatedDescription\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Category updated\""));

        verify(categoryService, times(1)).updateCategoryById(1, "UpdatedCategory", "UpdatedDescription");
    }

    @Test
    void updateCategory_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new CategoryNotFoundException("Category not found"))
                .when(categoryService).updateCategoryById(1, "UpdatedCategory", "UpdatedDescription");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedCategory\",\"description\":\"UpdatedDescription\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Category not found\""));
    }

    @Test
    void deleteCategory_shouldReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Category deleted\""));

        verify(categoryService, times(1)).deleteCategoryById(1);
    }

    @Test
    void deleteCategory_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new CategoryNotFoundException("Category not found"))
                .when(categoryService).deleteCategoryById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/categories/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Category not found\""));
    }
}
