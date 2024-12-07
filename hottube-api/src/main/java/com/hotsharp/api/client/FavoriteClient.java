package com.hotsharp.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("favorite-service")
public interface FavoriteClient {

}
