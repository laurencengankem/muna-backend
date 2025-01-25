package com.example.kulvida.controller.impl;

import com.example.kulvida.controller.ClothController;
import com.example.kulvida.domain.models.CartItem;
import com.example.kulvida.dto.request.*;
import com.example.kulvida.dto.response.AddItemListResponse;
import com.example.kulvida.dto.response.ClothAvailability;
import com.example.kulvida.dto.response.ClothDto;
import com.example.kulvida.dto.response.FileUploadResponse;
import com.example.kulvida.entity.cloth.*;
import com.example.kulvida.repository.*;
import com.google.gson.Gson;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ClothControllerImpl implements ClothController {

    @Autowired
    private ImageKit imageKit;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ClothRepository clothRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ClothSizeRepository clothSizeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClothPictureRepository clothPictureRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public ResponseEntity<?> save(@RequestBody FileUploadRequest request){
        log.info("upload call received");
        String url=null;
        try{
            FileCreateRequest fileCreateRequest= new FileCreateRequest(request.getImage(),request.getName());
            FileUploadResponse response= new FileUploadResponse();
            Cloth item= clothRepository.findById(request.getId()).orElse(null);
            if(item!=null){
                ClothPicture itemPicture= new ClothPicture();
                url =imageKit.upload(fileCreateRequest).getUrl();
                log.info("image url: {}",url);
                itemPicture.setUrl(url);
                item.getPictures().add(itemPicture);
                clothRepository.save(item);
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
        List<Cloth> items= clothRepository.findByName(request.getName());
        if(items.isEmpty() ){
            try{
                Cloth item= new Cloth();
                BeanUtils.copyProperties(request,item);
                if(item.getBrand()!=null)
                    item.setBrand(item.getBrand().toUpperCase(Locale.ROOT));
                if(request.getCategory()!=null){
                    Category category= categoryRepository.findByName(request.getCategory().toUpperCase(Locale.ROOT));
                    if(category==null)
                        category= categoryRepository.saveAndFlush(new Category(request.getCategory().toUpperCase(Locale.ROOT)));
                    item.setCategory(category);
                }
                item.setCreationDate(new GregorianCalendar());
                item=clothRepository.save(item);
                if(request.getSizes()!=null && !request.getSizes().isEmpty()){
                    List<ClothSize> clothSizeList= new ArrayList<>();
                    List<String> savedSizes= new ArrayList<>();
                    for(SizeRequest s: request.getSizes()){
                        Size size= sizeRepository.findByName(s.getName().toUpperCase(Locale.ROOT));
                        if(size==null)
                            size= sizeRepository.saveAndFlush(new Size(s.getName().toUpperCase(Locale.ROOT)));

                        if(savedSizes.contains(size.getName())){
                            continue;
                        }
                        savedSizes.add(size.getName());
                        ClothSize clothSize= new ClothSize();
                        clothSize.setSize(size);
                        clothSize.setCloth(item);
                        clothSize.setPrice(s.getPrice());
                        clothSize.setQuantity(s.getQuantity());
                        clothSizeList.add(clothSize);
                    }
                    clothSizeRepository.saveAll(clothSizeList);
                }
                return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
            }catch (Exception ex){
                log.info(ex.getMessage());
            }
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AddItemListResponse> addItemList(@RequestBody AddItemListRequest request){
        return null;
    }

    @Override
    public ResponseEntity<?> deleteItem(@PathVariable int idItem){
        log.info("Cloth to delete {}"+idItem);
        if(clothRepository.existsById(idItem)) {
            Cloth item= clothRepository.findById(idItem).orElse(null);
            item.setAvailable(false);
            clothRepository.save(item);
            return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?>  updateItem(@PathVariable int idItem, @RequestBody ClothUpdateRequest request){
        log.info("item to update: "+idItem);
        log.info("request {}",request);
        if(clothRepository.existsById(idItem)) {
            Cloth item= clothRepository.findById(idItem).orElse(null);
            if(request.getAvailable()!=null)
                item.setAvailable(request.getAvailable());
            if(request.getName()!=null)
                item.setName(request.getName());
            if(request.getDescription()!=null)
                item.setDescription(request.getDescription());
            if(request.getDiscount()!=null)
                item.setDiscount(request.getDiscount());
            if(request.getBrand()!=null)
                item.setBrand(request.getBrand().toUpperCase(Locale.ROOT));
            if(request.getSex()!=null)
                item.setSex(request.getSex());
            if(request.getCategory()!=null){
                Category category= categoryRepository.findByName(request.getCategory().toUpperCase(Locale.ROOT));
                if(category==null){
                    category= new Category(request.getCategory().toUpperCase(Locale.ROOT));
                    category= categoryRepository.saveAndFlush(category);
                }
                item.setCategory(category);
            }
            if(request.getSizes()!=null && !request.getSizes().isEmpty()){
                List<ClothSize> clothSizeList= new ArrayList<>();
                List<String> savedSizes= new ArrayList<>();
                for(SizeRequest s: request.getSizes()) {
                    Size size = sizeRepository.findByName(s.getName().toUpperCase(Locale.ROOT));
                    if (size == null)
                        size = sizeRepository.saveAndFlush(new Size(s.getName().toUpperCase(Locale.ROOT)));
                    if(savedSizes.contains(size.getName())){
                        continue;
                    }
                    savedSizes.add(size.getName());
                    ClothSize clothSize = new ClothSize();
                    clothSize.setSize(size);
                    clothSize.setCloth(item);
                    clothSize.setPrice(s.getPrice());
                    clothSize.setQuantity(s.getQuantity());
                    clothSizeList.add(clothSize);

                }
                List<ClothSize> oldSizes= clothSizeRepository.findByCloth(item);
                if(oldSizes!=null) {
                    clothSizeRepository.deleteAll(oldSizes);
                }
                item.getClothSizes().addAll(clothSizeList);
            }
            item.setLastUpdate(new GregorianCalendar());
            clothRepository.save(item);
            return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE,HttpStatus.OK);
    }


    @Override
    public ResponseEntity<List<ClothDto>> getItems(){
        List<Cloth> items= clothRepository.findAll();
        List<ClothDto> filteredItems= items.stream().map(ClothDto::new).collect(Collectors.toList());
        filteredItems= filteredItems.stream().filter(ClothDto::isAvailable).collect(Collectors.toList());
        filteredItems.forEach(item -> Collections.sort(item.getSizes()));
        return new ResponseEntity<List<ClothDto>>(filteredItems,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ClothDto>> getAllItems(){
        List<Cloth> items= clothRepository.findAll();
        List<ClothDto> filteredItems= items.stream().map(ClothDto::new).collect(Collectors.toList());
        return new ResponseEntity<List<ClothDto>>(filteredItems,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getPictures(@PathVariable String itemId){
        log.info("RetrievePictures of {}",itemId);
        try{
            int id= Integer.parseInt(itemId);
            List<String> pictures= clothPictureRepository.findPicturesUrls(id);
            return new ResponseEntity<List<String>>(pictures,HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<List<String>>(new ArrayList<String>(),HttpStatus.OK);
        }

    }


    @Override
    public ResponseEntity<?> searchItem(@RequestBody SearchItemsRequest request) {
        List<Cloth> items=null;
        List<Cloth> filteredItems=null;
        if(request.getTxt()==null){
            items= clothRepository.findAll();
            filteredItems= items.stream().filter(item -> item.getAvailable()).collect(Collectors.toList());
        }
        else{
            items=clothRepository.findByName(request.getTxt());
            filteredItems= items.stream().filter(item -> item.getAvailable()).collect(Collectors.toList());
        }
        Collections.sort(filteredItems);
        List<ClothDto> result= filteredItems.stream().map(ClothDto::new).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> getItem(@PathVariable int idItem){
        Cloth item= clothRepository.findById(idItem).orElse(null);
        if(item!=null){
            ClothDto cloth= new ClothDto(item);
            return ResponseEntity.ok(cloth);
        }
        return ResponseEntity.ok(item);
    }

    //TODO
    @Override
    public ResponseEntity<?> refreshCartItems(@RequestBody getCartItemsRequest request){

        Gson gson=new Gson();
        CartItem[] items= gson.fromJson(request.getData(),CartItem[].class);
        for(CartItem it: items){
            Cloth itm= clothRepository.findById(it.getId()).orElse(null);
            if(itm!=null){
                List<SizeRequest> sr=null;
                if(itm.getClothSizes()!=null && !itm.getClothSizes().isEmpty()){
                    sr= itm.getClothSizes().stream().map(SizeRequest::new).collect(Collectors.toList());
                    sr.stream().forEach(s->{if(s.getName().equalsIgnoreCase(it.getRequestedSize())){
                        it.setPrice(s.getPrice());
                    }});
                }
                it.setSizes(sr);
                it.setDiscount(itm.getDiscount());
                it.setDiscounted(it.getPrice()*(double)(1-((double)it.getDiscount()/100)));
                it.setTotal(it.getDiscounted()*it.getQuantity());
                it.setAvailable(true);
                if(!itm.getAvailable())
                    it.setAvailable(false);
                if(it.getPhoto()==null || it.getPhoto().length()==0){
                    if(itm.getPictures().size()>0)
                        it.setPhoto(itm.getPictures().get(0).getUrl());
                    for(ClothPicture pic: itm.getPictures()){
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
        ClothPicture itemPicture=clothPictureRepository.findPicture(request.getItemId(),request.getUrl());
        clothPictureRepository.delete(itemPicture);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<?> updateMainPicture(@PathVariable int idItem, @PathVariable int idImage){
        Cloth item= clothRepository.findById(idItem).orElse(null);
        if(item!=null){
            for(ClothPicture pic: item.getPictures()){
                if(pic.getTag()!=null && pic.getId()!=idImage && pic.getTag().equals("MAIN"))
                    pic.setTag(null);
                if(pic.getId()==idImage)
                    pic.setTag("MAIN");
            }
            clothRepository.save(item);
        }
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<?> searchItemByCode(SearchItemsRequest request) {
        try {
            int productId = Integer.parseInt(request.getCode().substring(0, 5));
            String size = request.getCode().substring(5);
            Cloth cloth= clothRepository.findById(productId).orElse(null);
            ClothAvailability cla= new ClothAvailability(cloth);
            cla.setRequestedSize(size);
            boolean available=false;
            if(cloth!=null){
                for(SizeRequest sr: cla.getSizes()){
                    if(sr.getName().equalsIgnoreCase(size)){
                        cla.setQuantity(sr.getQuantity());
                        if(sr.getQuantity()==0)
                            cla.setOutOfStock(true);
                        else {
                            available= true;
                            double price= ( sr.getPrice()/100 )* (double) cloth.getDiscount();
                            price= sr.getPrice() - price;
                            cla.setRequestedPrice(price);
                        }
                        break;
                    }
                }
                if(!available){
                    cla.setAvailable(false);
                }
            }
            return ResponseEntity.ok(cla);

        } catch (Exception e) {
            return (ResponseEntity<?>) ResponseEntity.badRequest();
        }

    }

    @Override
    public ResponseEntity<?> searchAllItems(SearchItemsRequest request) {

        Integer productId= null;
        List<Cloth> items= new ArrayList<>();
        if(request.getTxt()!=null && request.getTxt().matches("\\d+")) {
            productId = Integer.parseInt(request.getTxt());
            Cloth cloth= clothRepository.findById(productId).orElse(null);
            if(cloth!=null){
                if(cloth.getAvailable())
                    items.add(cloth);
                else if(!cloth.getAvailable() && request.getAll()==null)
                    items.add(cloth);
            }
        }else{
            items=clothRepository.searchByInput(request.getTxt());
            if(request.getAll()!=null)
                items= items.stream().filter(Cloth::getAvailable).collect(Collectors.toList());
        }

        List<ClothDto> result= items.stream().map(ClothDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> getItemsByCategory(String sex, String category) {
        Category cat= categoryRepository.findByName(category.toUpperCase(Locale.ROOT));
        Sex s= Sex.valueOf(sex.toUpperCase(Locale.ROOT));
        List<Cloth> items= clothRepository.findByCategoryAndSex(cat,s);
        List<ClothDto> result= items.stream().map(ClothDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }


}
