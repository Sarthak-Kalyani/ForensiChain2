package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.model.Consent;
import com.sdcems.sdcems.repository.ConsentRepository;
import com.sdcems.sdcems.service.HashService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consent")
public class ConsentController {

    private final ConsentRepository repo;

    public ConsentController(ConsentRepository repo){
        this.repo = repo;
    }

    @PostMapping("/create")
    public Consent create(@RequestBody Consent consent){
        consent.setHashValue(HashService.generateHash(consent.getContent()));
        return repo.save(consent);
    }

    @GetMapping("/verify/{id}")
    public String verify(@PathVariable int id){
        Consent c = repo.findById(id).orElseThrow();
        String hash = HashService.generateHash(c.getContent());

        if(hash.equals(c.getHashValue()))
            return "VALID - Not Tampered";
        else
            return "TAMPERED";
    }
}