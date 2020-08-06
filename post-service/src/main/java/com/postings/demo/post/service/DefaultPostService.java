package com.postings.demo.post.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postings.demo.post.client.UserClient;
import com.postings.demo.post.model.Hashtag;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.model.User;
import com.postings.demo.post.repository.HashtagRepository;
import com.postings.demo.post.repository.PostRepository;

@Service
public class DefaultPostService implements PostService {

	@Autowired
	private PostRepository postRepository ;
	
	@Autowired
	private HashtagRepository hashtagRepository ;
	
	@Autowired
	private UserClient userClient ;

	@Override
	public Optional<Post> findById(long id) {
		return postRepository.findById(id);
	}
	
	public Page<Post> findByUserId(String userId, Pageable pageable) {
		return postRepository.findByUserId(userId, pageable) ;
	}
	
	public List<Post> findByUserIdOrIsPublic(String userId, boolean isPublic) {
		return postRepository.findByUserIdOrIsPublic(userId, isPublic) ;
	}

	@Override
	@Transactional
	public Post save(Post post) {
		post.setCreatedAt(LocalDateTime.now());
		post.setEditedAt(post.getCreatedAt());
		Post createdPost = postRepository.save(post);
		if (createdPost != null) {
			updateUserPostCount(createdPost) ;
			updateHashtags(createdPost);
		}
		return createdPost ;
	}

	@Override
	@Transactional
	public Post update(Post post) {
		post.setEditedAt(LocalDateTime.now());
		Post updatedPost = postRepository.save(post);
		if (updatedPost != null) {
			updateHashtags(updatedPost);
		}
		return updatedPost ;
	}
	@Override
	public void delete(Long id) {
		postRepository.deleteById(id);
	}
	
	private void updateUserPostCount(Post post) {
		Optional<User> user = userClient.getUser(post.getUserId()) ;
		if (user.isPresent()) {
			user.get().setPosts(postRepository.findCountByUserId(post.getUserId()));
			userClient.updateUser(post.getUserId(), user.get());
		}
	}
	
	private void updateHashtags(Post updatedPost) {
		hashtagRepository.deleteByPostId(updatedPost.getId());
		if (updatedPost.getHashtags() != null && updatedPost.getHashtags().size() > 0) {
			updatedPost.getHashtags().forEach(h -> hashtagRepository.save(new Hashtag(h, updatedPost.getId())));
		}
	}
}
