package com.example.kulvida.controller.impl;

import com.example.kulvida.controller.ClothController;
import com.example.kulvida.domain.models.CartItem;
import com.example.kulvida.dto.request.*;
import com.example.kulvida.dto.response.AddItemListResponse;
import com.example.kulvida.dto.response.ClothAvailability;
import com.example.kulvida.dto.response.ClothDto;
import com.example.kulvida.dto.response.FileUploadResponse;
import com.example.kulvida.entity.ItemPicture;
import com.example.kulvida.entity.UserItem;
import com.example.kulvida.entity.cloth.*;
import com.example.kulvida.repository.*;
import com.google.gson.Gson;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Value("${ItemPictureDir}")
    private String picDir;

    @Value("${ImageBaseUrl}")
    private String  picBaseUrl;


    @Override
    public ResponseEntity<?> save(@RequestBody FileUploadRequest request){
        String url=null;
        try{
            FileCreateRequest fileCreateRequest= new FileCreateRequest(request.getImage(),request.getName());
            Cloth item= clothRepository.findById(request.getId()).orElse(null);
            /*if(item!=null){
                ClothPicture itemPicture= new ClothPicture();
                url =imageKit.upload(fileCreateRequest).getUrl();
                log.info("image url: {}",url);
                itemPicture.setUrl(url);
                item.getPictures().add(itemPicture);
                clothRepository.save(item);
                return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
            }*/
            if(item!=null){
                log.info(request.getImage());
                String fileName= item.getPictures().isEmpty()?
                        item.getCode().toLowerCase(Locale.ROOT): item.getCode().toLowerCase(Locale.ROOT)+"_"+item.getPictures().size();
                fileName=this.saveImage(request.getImage(),fileName);
                ClothPicture itemPicture= new ClothPicture();
                itemPicture.setUrl(picBaseUrl+fileName);
                item.getPictures().add(itemPicture);
                clothRepository.save(item);
                return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
    }

    private String saveImage(String base64Image, String fileName) throws Exception{
        if (!base64Image.startsWith("data:image")) {
            throw new Exception("Invalid image format");
        }

        String[] parts = base64Image.split(",");
        if (parts.length != 2) {
            throw new Exception("Malformed base64 string");
        }

        String extension = parts[0].contains("jpeg") ? "jpg" : "png";
        byte[] imageBytes = Base64.getDecoder().decode(parts[1]);

        File directory = new File(picDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageFile = new File(picDir, fileName+"."+ extension);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageBytes);
        }
        return fileName+"."+ extension;
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
                if(item.getColor()!=null)
                    item.setColor(item.getColor().toUpperCase(Locale.ROOT));
                if(request.getCategory()!=null){
                    Category category= categoryRepository.findByName(request.getCategory().toUpperCase(Locale.ROOT));
                    if(category==null)
                        category= categoryRepository.saveAndFlush(new Category(request.getCategory().toUpperCase(Locale.ROOT)));
                    item.setCategory(category);
                }
                item.setCode(item.getCode().toUpperCase(Locale.ROOT));
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
                        clothSize.setMagasin(s.getMagasin());
                        clothSize.setCloth(item);
                        clothSize.setPrice(s.getPrice());
                        clothSize.setQuantity(s.getQuantity());
                        clothSize.setLocation(s.getLocation());
                        clothSizeList.add(clothSize);
                    }
                    clothSizeRepository.saveAll(clothSizeList);
                }
                return new ResponseEntity<Integer>(item.getClothId(),HttpStatus.OK);
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
            List<UserItem> userItems=   userItemRepository.findByItem(item);
            userItemRepository.deleteAll(userItems);
            clothRepository.delete(item);
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
            if(request.getColor()!=null)
                item.setColor(request.getColor().toUpperCase(Locale.ROOT));
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
            if(request.getCode()!=null)
                item.setCode(request.getCode());
            if(request.getCost()!=null)
                item.setCost(request.getCost());
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
                    clothSize.setMagasin(s.getMagasin());
                    clothSize.setLocation(s.getLocation());
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

            String code=request.getCode().trim().substring(0,5);
            //int productId = Integer.parseInt(request.getCode().substring(0, 5));
            String size = request.getCode().trim().length()>5 ? request.getCode().trim().substring(5): "";
            ClothAvailability cla= null;
            Cloth cloth= clothRepository.findByCode(code.toUpperCase(Locale.ROOT));
            boolean available=false;
            if(cloth!=null && cloth.getAvailable()){
                cla= new ClothAvailability(cloth);
                cla.setRequestedSize(size.toUpperCase(Locale.ROOT));
                for(SizeRequest sr: cla.getSizes()){
                    if(size.isEmpty()){
                        cla.setRequestedSize(null);
                        cla.setRequestedPrice(null);
                        if(sr.getQuantity()>0){
                            double price= ( sr.getPrice()/100 )* (double) cloth.getDiscount();
                            price= sr.getPrice() - price;
                            sr.setPrice(price);
                            cla.getAvailableSizes().add(sr);
                        }
                    }
                    else if(sr.getName().equalsIgnoreCase(size)){
                        cla.setQuantity(sr.getQuantity());
                        if(sr.getQuantity()==0)
                            cla.setOutOfStock(true);
                        else {
                            available= true;
                            double price= ( sr.getPrice()/100 )* (double) cloth.getDiscount();
                            price= sr.getPrice() - price;
                            cla.setRequestedPrice(price);
                            cla.setLocation(sr.getLocation());
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

        List<Cloth> items= new ArrayList<>();
        if(request.getTxt()!=null) {
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


    @Override
    public ResponseEntity<?> getItemPicture(String fileName) throws MalformedURLException {
        Path imagePath = Paths.get(picDir).resolve(fileName).normalize();
        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = getContentType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }


    private Optional<String> getContentType(String filename) {
        if (filename.endsWith(".png")) {
            return Optional.of(MediaType.IMAGE_PNG_VALUE);
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return Optional.of(MediaType.IMAGE_JPEG_VALUE);
        }
        return Optional.empty();
    }

    @Override
    public ResponseEntity<?> setProductCode(){
        List<OrderItem> orderItems= orderItemRepository.findAll();

        List<Cloth> clothes= clothRepository.findAll();

        List<ClothSize> clothSizes= clothSizeRepository.findAll();

        clothSizes.stream().filter(cs -> cs.getMagasin()==null).forEach(cs -> cs.setMagasin(0));

        clothSizeRepository.saveAllAndFlush(clothSizes);


        /*clothes.forEach(cloth -> {
            if(cloth.getCode().length()==8){
                cloth.setCode("0"+cloth.getCode().substring(4));
            }
            else{
                String padded= "0000"+cloth.getCode();
                cloth.setCode(padded.substring(padded.length()-5));
            }
        });
        clothRepository.saveAll(clothes);

        for(OrderItem order: orderItems){
            Cloth cloth= clothRepository.findById(order.getCloth()).orElse(null);
            if(cloth!=null){
                order.setClothName(cloth.getName());
                order.setClothCode(cloth.getCode());
            }
        }

        orderItemRepository.saveAllAndFlush(orderItems);*/

        return ResponseEntity.ok(clothSizes.size());
    }

    @Override
    public ResponseEntity<?> downloadPics(){
        List<ClothPicture> pictures= clothPictureRepository.findAll();

        for(ClothPicture pic: pictures){
            if(pic.getUrl()!=null){
                String[] parts= pic.getUrl().split("/");
                String fileName = pic.getUrl().split("/")[parts.length-1];
                try {
                    downloadImage(pic.getUrl(), picDir, fileName);
                    log.info("Download complete: " + picDir + "/" + fileName);
                    pic.setUrl(picBaseUrl+fileName);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        clothPictureRepository.saveAllAndFlush(pictures);

        return ResponseEntity.ok(pictures.size());
    }

    private void downloadImage(String imageUrl, String saveDir, String fileName) throws IOException {
        URL url = new URL(imageUrl);
        Path filePath = Paths.get(saveDir, fileName);
        Files.createDirectories(Paths.get(saveDir));

        try (InputStream in = url.openStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public ResponseEntity<?> editPicsUrl(String oldIp, String newIp) {

        if (!isValidIp(oldIp) || !isValidIp(newIp)) {
            return ResponseEntity.badRequest().body("Wrong IP format");
        }

        List<ClothPicture> pictures = clothPictureRepository.findAll();

        List<ClothPicture> updatedPictures = pictures.stream()
                .filter(pic -> pic.getUrl() != null && pic.getUrl().contains(oldIp))
                .peek(pic -> pic.setUrl(pic.getUrl().replace(oldIp, newIp)))
                .collect(Collectors.toList());

        clothPictureRepository.saveAll(updatedPictures);

        return ResponseEntity.ok(
                updatedPictures.size() + " pictures updated successfully"
        );
    }


    private boolean isValidIp(String ip){
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (Exception e){
            return false;
        }
    }



}
