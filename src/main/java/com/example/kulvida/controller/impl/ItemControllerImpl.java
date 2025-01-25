package com.example.kulvida.controller.impl;

import com.example.kulvida.controller.ItemController;
import com.example.kulvida.domain.models.CartItem;
import com.example.kulvida.dto.request.*;
import com.example.kulvida.dto.response.AddItemListResponse;
import com.example.kulvida.dto.response.FileUploadResponse;
import com.example.kulvida.entity.Item;
import com.example.kulvida.entity.ItemPicture;
import com.example.kulvida.entity.User;
import com.example.kulvida.entity.UserItem;
import com.example.kulvida.repository.ItemPictureRepository;
import com.example.kulvida.repository.ItemRepository;
import com.example.kulvida.repository.UserItemRepository;
import com.example.kulvida.repository.UserRepository;
import com.google.gson.Gson;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
//@RestController
public class ItemControllerImpl implements ItemController {

    @Autowired
    private ImageKit imageKit;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemPictureRepository itemPictureRepository;
    
    @Autowired
    private UserItemRepository userItemRepository;
    
    @Autowired
    private UserRepository userRepository;


    @Override
    public ResponseEntity<?> save(@RequestBody FileUploadRequest request){
        log.info("upload call received");
        String url=null;
        try{
            FileCreateRequest fileCreateRequest= new FileCreateRequest(request.getImage(),request.getName());
            FileUploadResponse response= new FileUploadResponse();
            Item item= itemRepository.findById(request.getId()).orElse(null);
            if(item!=null){
                ItemPicture itemPicture= new ItemPicture();
                //itemPicture.setItem(item);
                url =imageKit.upload(fileCreateRequest).getUrl();
                log.info("image url: {}",url);
                itemPicture.setUrl(url);
                item.getPictures().add(itemPicture);
                itemRepository.save(item);
                //itemPicture.setUrl(url);
                //itemPictureRepository.save(itemPicture);
                return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
        //imageKit.deleteFile();
    }

    @Override
    public ResponseEntity<?> addItem(@RequestBody AddItemRequest request){
        log.info(request.toString());
        List<Item> items= itemRepository.findByName(request.getName());
        if(items.isEmpty() ){
            try{
                Item item= new Item();
                BeanUtils.copyProperties(request,item);
                item.setCreationDate(new GregorianCalendar());
                itemRepository.save(item);
                return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
            }catch (Exception ex){
                log.debug("unable to add the item to the catalog");
            }
        }
        else{
            items.get(0).setAvailable(true);
            BeanUtils.copyProperties(request,items.get(0));
            itemRepository.save(items.get(0));
            return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
        }
        return new ResponseEntity<Boolean>(Boolean.FALSE,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AddItemListResponse> addItemList(@RequestBody AddItemListRequest request){

        AddItemListResponse response=new AddItemListResponse();
        try{
            log.info(request.getItems());
            Gson gson=new Gson();
            Item[] items= gson.fromJson(request.getItems(),Item[].class);
            log.info("{}",items);
            Long num1= Arrays.stream(items).filter(item->!itemRepository.existsByName(item.getName())).count();
            Arrays.stream(items).filter(item ->!itemRepository.existsByName(item.getName()))
                    .forEach(item -> { item.setCreationDate(new GregorianCalendar());itemRepository.save(item); });

            Long num2= Arrays.stream(items).filter(item ->itemRepository.existsByName(item.getName()) && !itemRepository.checkAvalability(item.getName())).count();
            Arrays.stream(items).filter(item ->itemRepository.existsByName(item.getName()) && !itemRepository.checkAvalability(item.getName()))
                    .forEach(item -> {
                        List<Item> it= itemRepository.findByName(item.getName());
                        it.get(0).setAvailable(true);
                        it.get(0).setQuantity(item.getQuantity()+it.get(0).getQuantity());
                        it.get(0).setLastUpdate(new GregorianCalendar());
                        itemRepository.save(it.get(0)); }
                    );
            response.setMsg((num1+num2)+ " new items added to the catalog");

            return new ResponseEntity<AddItemListResponse>(response,HttpStatus.OK);
        }
        catch (Exception ex){
            response.setMsg("Bad Json File");
            return new ResponseEntity<AddItemListResponse>(response,HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> deleteItem(@PathVariable int idItem){
        log.info("item to delete {}"+idItem);
        if(itemRepository.existsById(idItem)) {
            Item item= itemRepository.findById(idItem).orElse(null);
            item.setAvailable(false);
            itemRepository.save(item);
            return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?>  updateItem(@PathVariable int idItem, @RequestBody Item item){
        log.info("item to update: "+idItem);
        log.info("request {}",item);
        if(itemRepository.existsById(idItem)) {
            Item it= itemRepository.findById(idItem).orElse(null);
            if(item.getPrice()!=null && item.getPrice()>0)
                it.setPrice(item.getPrice());
            if(item.getDescription()!=null)
                it.setDescription(item.getDescription());
            if(item.getName()!=null)
                it.setName(item.getName());
            if(item.getQuantity()!=null &&item.getQuantity()>0)
                it.setQuantity(item.getQuantity());
            if(item.getDiscount()!=null)
                it.setDiscount(item.getDiscount());
            if(item.getCategory()!=null)
                it.setCategory(item.getCategory().toUpperCase(Locale.ROOT));

            it.setLastUpdate(new GregorianCalendar());
            itemRepository.save(it);
            return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE,HttpStatus.OK);
    }


    @Override
    public ResponseEntity<List<Item>> getItems(){
        List<Item> items= itemRepository.findAll();
        List<Item> filteredItems= items.stream().filter(item -> item.getAvailable()).collect(Collectors.toList());
        filteredItems.forEach(item -> {item.setDiscounted();});
        Collections.sort(items);
        return new ResponseEntity<List<Item>>(filteredItems,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getPictures(@PathVariable String itemId){
        log.info("RetrievePictures of {}",itemId);
        try{
            int id= Integer.parseInt(itemId);
            List<String> pictures= itemPictureRepository.findPicturesUrls(id);
            return new ResponseEntity<List<String>>(pictures,HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<List<String>>(new ArrayList<String>(),HttpStatus.OK);
        }

    }


    @Override
    public ResponseEntity<?> searchItem(@RequestBody SearchItemsRequest request) {
        List<Item> items=null;
        List<Item> filteredItems=null;
        if(request.getTxt()==null){
            items= itemRepository.findAll();
            filteredItems= items.stream().filter(item -> item.getAvailable()).collect(Collectors.toList());
        }
        else{
            items=itemRepository.findByName(request.getTxt());
            filteredItems= items.stream().filter(item -> item.getAvailable()).collect(Collectors.toList());
        }
        filteredItems.forEach(item -> item.setDiscounted());
        filteredItems.stream().forEach(item -> item.isnew());
        Collections.sort(filteredItems);
        return ResponseEntity.ok(filteredItems);
    }

    @Override
    public ResponseEntity<?> getItem(@PathVariable int idItem){
        Item item= itemRepository.findById(idItem).orElse(null);
        if(item!=null)
            item.setDiscounted();
        return ResponseEntity.ok(item);
    }

    @Override
    public ResponseEntity<?> refreshCartItems(@RequestBody getCartItemsRequest request){

        Gson gson=new Gson();
        CartItem[] items= gson.fromJson(request.getData(),CartItem[].class);
        for(CartItem it: items){
            Item itm= itemRepository.findById(it.getId()).orElse(null);
            if(itm!=null){
                itm.setDiscounted();
                it.setDiscounted(itm.getDiscounted());
                it.setPrice(itm.getPrice());
                it.setDiscount(itm.getDiscount());
                it.setTotal(it.getDiscounted()*it.getQuantity());
                it.setAvailable(true);
                if(itm.getQuantity()<0)
                    it.setAvailable(false);
                if(it.getPhoto()==null || it.getPhoto().length()==0){
                    if(itm.getPictures().size()>0)
                        it.setPhoto(itm.getPictures().get(0).getUrl());
                    for(ItemPicture pic: itm.getPictures()){
                        if(pic.getTag()!=null && pic.getTag().equals("MAIN"))
                            it.setPhoto(pic.getUrl());
                    }
                }

            }
            else{
                it.setAvailable(false);
            }

        }
        log.info("{}",items);
        return ResponseEntity.ok(gson.toJson(items));

    }

    @Override
    public ResponseEntity<?> deletePicture(@RequestBody DeletePictureRequest request){
        log.info(request.toString());
        ItemPicture itemPicture=itemPictureRepository.findPicture(request.getItemId(),request.getUrl());
        itemPictureRepository.delete(itemPicture);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<?> updateMainPicture(@PathVariable int idItem, @PathVariable int idImage){
        Item item= itemRepository.findById(idItem).orElse(null);
        if(item!=null){
            for(ItemPicture pic: item.getPictures()){
                if(pic.getTag()!=null && pic.getId()!=idImage && pic.getTag().equals("MAIN"))
                    pic.setTag(null);
                if(pic.getId()==idImage)
                    pic.setTag("MAIN");
            }
            itemRepository.save(item);
        }
        return ResponseEntity.ok(true);
    }



}
