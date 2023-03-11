package com.restaurant.restaurantbackend.emailhelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Attachment {

    private String name;

    private byte[] content;
}
