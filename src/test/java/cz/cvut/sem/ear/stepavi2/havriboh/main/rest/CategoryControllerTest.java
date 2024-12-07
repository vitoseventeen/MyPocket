package cz.cvut.sem.ear.stepavi2.havriboh.main.rest;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.service.CategoryService;
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
        Category category1 = new Category();
        category1.setName("Category1");
        category1.setDescription("Description1");
        category1.setDefaultLimit(BigDecimal.TEN);

        Category category2 = new Category();
        category2.setName("Category2");
        category2.setDescription("Description2");
        category2.setDefaultLimit(BigDecimal.valueOf(20));

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].name").value("Category1"))
                .andExpect(jsonPath("$[1].name").value("Category2"));
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category category = new Category();
        category.setName("Category1");
        category.setDescription("Description1");
        category.setDefaultLimit(BigDecimal.TEN);

        when(categoryService.getCategoryById(1)).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("Category1"));
    }

    @Test
    void getCategoryById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new RuntimeException("Category not found")).when(categoryService).getCategoryById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Category not found"));
    }

    @Test
    void createCategory_shouldCreateAndReturn201() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Category1\",\"description\":\"Description1\",\"defaultLimit\":10}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Category created"));

        verify(categoryService, times(1))
                .createCategory("Category1", "Description1", BigDecimal.TEN);
    }

    @Test
    void updateCategory_shouldUpdateAndReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/rest/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedCategory\",\"description\":\"UpdatedDescription\",\"defaultLimit\":15}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Category updated"));

        verify(categoryService, times(1))
                .updateCategoryById(1, "UpdatedCategory", "UpdatedDescription", BigDecimal.valueOf(15));
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
        doThrow(new RuntimeException("Category not found")).when(categoryService).deleteCategoryById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Category not found"));
    }
}
