package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.Metadata;
import pers.juumii.dto.hub.MetadataDTO;
import pers.juumii.service.StorageService;

import java.io.IOException;
import java.util.List;

@RestController
public class StorageController {

    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/user/{userId}")
    public MetadataDTO push(
            @PathVariable Long userId,
            @RequestParam("title") String title,
            @RequestParam("contentType") String contentType,
            @RequestParam("file") MultipartFile file) throws IOException {
       return Metadata.transfer(storageService.push(userId, title, file.getInputStream(), contentType));
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<byte[]> pull(@PathVariable Long resourceId){
        return storageService.pull(resourceId);
    }

    @DeleteMapping("/resource/{resourceId}")
    public void remove(@PathVariable Long resourceId){
        storageService.remove(resourceId);
    }

    @GetMapping("/user/{userId}/metadata")
    public List<Metadata> getMetadataList(@PathVariable Long userId){
        return storageService.getMetadataList(userId);
    }

    @RequestMapping(value = "/resource/{resourceId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> exists(@PathVariable Long resourceId){
        return storageService.exists(resourceId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }


    @GetMapping("/hello")
    public String hello(){
        return "hello mindtrace hub";
    }

}
