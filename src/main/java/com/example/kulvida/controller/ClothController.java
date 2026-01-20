package com.example.kulvida.controller;

import com.example.kulvida.dto.request.*;
import com.example.kulvida.dto.response.AddItemListResponse;
import com.example.kulvida.dto.response.ClothDto;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

public interface ClothController {

    @PostMapping("operator/uploadPictures")
    @CrossOrigin
    ResponseEntity<?> save(@RequestBody FileUploadRequest request);

    @PostMapping("operator/addItem")
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
    ResponseEntity<?> updateItem(@PathVariable int idItem, @RequestBody ClothUpdateRequest request);

    @GetMapping("item/getItemsList")
    @CrossOrigin
    ResponseEntity<List<ClothDto>> getItems();

    @GetMapping("item/getAllItems")
    @CrossOrigin
    ResponseEntity<List<ClothDto>> getAllItems();

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

    @PostMapping("item/searchItemByCode")
    @CrossOrigin
    ResponseEntity<?> searchItemByCode(@RequestBody SearchItemsRequest request);

    @PostMapping("item/searchAllItems")
    @CrossOrigin
    ResponseEntity<?> searchAllItems(@RequestBody SearchItemsRequest request);


    @GetMapping("item/{sex}/{category}")
    @CrossOrigin
    ResponseEntity<?> getItemsByCategory(
            @PathVariable String sex,
            @PathVariable String category
    );



    @GetMapping("item/setCodes")
    @CrossOrigin
    ResponseEntity<?> setProductCode();

    @GetMapping("item/getPics")
    @CrossOrigin
    ResponseEntity<?> downloadPics();

    @GetMapping("item/getPicture/{fileName}")
    @CrossOrigin
    ResponseEntity<?> getItemPicture(@PathVariable String fileName) throws MalformedURLException;


}
