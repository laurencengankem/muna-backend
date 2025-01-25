package com.example.kulvida.controller;

import com.example.kulvida.dto.request.*;
import com.example.kulvida.dto.response.AddItemListResponse;
import com.example.kulvida.entity.Item;
import com.example.kulvida.entity.UserItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



public interface ItemController {
    @PostMapping("admin/uploadPictures")
    @CrossOrigin
    ResponseEntity<?> save(@RequestBody FileUploadRequest request);

    @PostMapping("admin/addItem")
    @CrossOrigin
    ResponseEntity<?> addItem(@RequestBody AddItemRequest request);

    @PostMapping("admin/item/addItemList")
    @CrossOrigin
    ResponseEntity<AddItemListResponse> addItemList(@RequestBody AddItemListRequest request);

    @PostMapping("admin/deleteItem/{idItem}")
    @CrossOrigin
    ResponseEntity<?> deleteItem(@PathVariable int idItem);

    @PostMapping("admin/updateItem/{idItem}")
    @CrossOrigin
    ResponseEntity<?> updateItem(@PathVariable int idItem, @RequestBody Item item);

    @GetMapping("item/getItemsList")
    @CrossOrigin
    ResponseEntity<List<Item>> getItems();

    @GetMapping("admin/getPictures/{itemId}")
    @CrossOrigin
    ResponseEntity<List<String>> getPictures(@PathVariable String itemId);

    @PostMapping("item/searchItems")
    @CrossOrigin
    ResponseEntity<?> searchItem(@RequestBody SearchItemsRequest request);

    @GetMapping("item/searchItems/{idItem}")
    @CrossOrigin
    ResponseEntity<?> getItem(@PathVariable int idItem);

    @PostMapping("item/getCartItems")
    @CrossOrigin
    ResponseEntity<?> refreshCartItems(@RequestBody getCartItemsRequest request);

    @PostMapping("admin/deletePicture")
    @CrossOrigin
    ResponseEntity<?>deletePicture(@RequestBody DeletePictureRequest request);

    @GetMapping("admin/updateMainPicture/{idItem}/{idImage}")
    @CrossOrigin
    ResponseEntity<?> updateMainPicture(@PathVariable int idItem, @PathVariable int idImage);


}
