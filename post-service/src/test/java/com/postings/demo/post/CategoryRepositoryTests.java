package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_DESCRIPTION;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_TITLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.postings.demo.post.model.Category;
import com.postings.demo.post.repository.CategoryRepository;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@Transactional
public class CategoryRepositoryTests {

	@Autowired
	private CategoryRepository categoryRepository ;
	
	@Test
	@DisplayName("findById Success")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryIdWhenFindByIdThenSuccess() {
		Optional<Category> category = categoryRepository.findById(CATEGORY_ID) ;
		assertThat(category).isPresent() ;
	}
	
	@Test
	@DisplayName("findById NotFound")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenNotExistingCategoryIdWhenFindByIdThenNotFound() {
		Optional<Category> category = categoryRepository.findById(CATEGORY_ID + 10) ;
		assertThat(category).isEmpty() ;
	}
	
	@Test
	@DisplayName("save Success")
	public void givenCategoryWhenIsSavedThenSuccess() {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		Category savedCategory = categoryRepository.save(category);
		
		assertThat(savedCategory).isNotNull() ;
		assertThat(savedCategory.getId()).isNotNull() ;
		assertThat(savedCategory.getTitle()).isNotNull().isEqualTo(CATEGORY_TITLE) ;
		assertThat(savedCategory.getDescription()).isNotNull().isEqualTo(CATEGORY_DESCRIPTION) ;
	}
	
	@Test
	@DisplayName("update Success")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryWhenIsUpdatedThenSuccess() {
		Category category = new Category(0L, "new" + CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		Category savedCategory = categoryRepository.save(category);
		
		assertThat(savedCategory).isNotNull() ;
		assertThat(savedCategory.getId()).isNotNull() ;
		assertThat(savedCategory.getTitle()).isNotNull().isEqualTo("new" + CATEGORY_TITLE) ;
		assertThat(savedCategory.getDescription()).isNotNull().isEqualTo(CATEGORY_DESCRIPTION) ;
	}
	
	@Test
	@DisplayName("delete Success")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryIdWhenIsDeletedThenSuccess() {
		categoryRepository.deleteById(CATEGORY_ID) ;
	}
}
