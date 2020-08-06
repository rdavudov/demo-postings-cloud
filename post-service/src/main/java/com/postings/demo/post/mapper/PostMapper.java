package com.postings.demo.post.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.model.Hashtag;
import com.postings.demo.post.model.Post;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userId", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "editedAt", ignore = true)
	@Mapping(source = "hashtags", target = "hashtags", qualifiedByName = "toHashtags")
	@Mapping(source = "categoryId", target = "category", qualifiedByName = "toCategory")
	Post toPost(PostDto dto) ;

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "editedAt", ignore = true)
	@Mapping(target = "userId", ignore = true)
	@Mapping(source = "hashtags", target = "hashtags", qualifiedByName = "toHashtags")
	@Mapping(source = "categoryId", target = "category", qualifiedByName = "toCategory")
	void toPost(PostDto dto, @MappingTarget Post post) ;
	
	@Mapping(source = "createdAt", target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(source = "editedAt", target = "editedAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(source = "hashtags", target = "hashtags", qualifiedByName = "fromHashtags")
	@Mapping(source = "category", target = "categoryId", qualifiedByName = "toCategory")
	PostDto toDto(Post post) ;
	
	// inside mapper generated code it tries to clear target entity list and then call add all using list in dto
	// since retrieved post entity has immutable list it throws an exception
	// by below logic we are forcing mapstruct to directly set values provided by us without clearing and adding all
	@Named("toHashtags")
    public static List<String> toHashtags(List<String> hashtags) {
		return hashtags ;
    }
	
	@Named("fromHashtags")
    public static List<String> fromHashtags(List<String> hashtags) {
		return hashtags ;
    }
	
	@Named("toCategory")
    public static Category toCategory(long categoryId) {
		Category category = new Category() ;
		category.setId(categoryId);
        return category ;
    }
	
	@Named("fromCategory")
    public static long toCategory(Category category) {
        return category.getId() ;
    }
}
