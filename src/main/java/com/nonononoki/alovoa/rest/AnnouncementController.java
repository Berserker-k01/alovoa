package com.nonononoki.alovoa.rest;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nonononoki.alovoa.model.AlovoaException;
import com.nonononoki.alovoa.model.AnnouncementDto;
import com.nonononoki.alovoa.service.AnnouncementService;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/nearby")
    public List<AnnouncementDto> nearby(@RequestParam(required = false) String category) throws AlovoaException {
        return announcementService.findNearby(category);
    }

    @GetMapping("/mine")
    public List<AnnouncementDto> mine() throws AlovoaException {
        return announcementService.findOwn();
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam String category,
                                         @RequestParam String title,
                                         @RequestParam String text,
                                         @RequestParam(required = false) String place,
                                         @RequestParam(required = false) Long eventDate) throws AlovoaException {
        Date date = eventDate != null ? new Date(eventDate) : null;
        announcementService.create(category, title, text, place, date);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws AlovoaException {
        announcementService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
