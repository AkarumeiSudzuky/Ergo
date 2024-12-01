package com.ergo.Springserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for handling friend requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    private Long userId;
    private Long friendId;
}
