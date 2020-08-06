package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_DESCRIPTION;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.postings.demo.post.model.Category;
import com.postings.demo.post.repository.CategoryRepository;
import com.postings.demo.post.service.CategoryService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CategoryServiceTests {

	@Autowired
	private CategoryService categoryService ;
	
	@MockBean
	private CategoryRepository categoryRepository ;
	
	@Test
	@DisplayName("findById Success")
	public void givenPostIdWhenFindByIdThenSuccess() {
		doReturn(Optional.of(new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION))).when(categoryRepository).findById(CATEGORY_ID) ;
		Optional<Category> category = categoryService.findById(CATEGORY_ID) ;
		assertThat(category).isPresent() ;
		verify(categoryRepository, times(1)).findById(CATEGORY_ID) ;
	}
	
	@Test
	@DisplayName("findById NotFound")
	public void givenNotExistingPostIdWhenFindByIdThenNotFound() {
		doReturn(Optional.empty()).when(categoryRepository).findById(CATEGORY_ID) ;
		Optional<Category> category = categoryService.findById(CATEGORY_ID) ;
		assertThat(category).isEmpty() ;
		verify(categoryRepository, times(1)).findById(CATEGORY_ID) ;
	}
	
	@Test
	@DisplayName("save Success")
	public void givenPostWhenIsSavedThenSuccess() {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		doReturn(category).when(categoryRepository).save(any()) ;
		
		Category savedCategory = categoryService.save(category);
		
		assertThat(savedCategory).isNotNull() ;
		assertThat(savedCategory.getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(savedCategory.getTitle()).isNotNull().isEqualTo(CATEGORY_TITLE) ;
		assertThat(savedCategory.getDescription()).isNotNull().isEqualTo(CATEGORY_DESCRIPTION) ;
	}
	
	@Test
	@DisplayName("delete Success")
	public void givenPostIdWhenIsDeletedThenSuccess() {
		doNothing().when(categoryRepository).deleteById(anyLong());
		categoryService.delete(CATEGORY_ID) ;
		verify(categoryRepository).deleteById(CATEGORY_ID);
	}
}
