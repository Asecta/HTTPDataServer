package io.asecta.service.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "posts")

@Getter
@Setter
public class Post {

	@Id @GeneratedValue private int id;
	private String title;
	private String content;
	private String[] imageUrls;
	
	private String[] tags; 
	
	@OneToOne
	private User author;
	
}
