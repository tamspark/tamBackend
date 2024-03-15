package com.sparklab.TAM.contollers;

import com.sparklab.TAM.dto.MeetingLinkDTO;
import com.sparklab.TAM.model.ApartmentOption;
import com.sparklab.TAM.services.MeetingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("TAM/meeting")
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/generateJitsiMeetLink")
    public ResponseEntity<?> generateMeetingLink(@RequestBody MeetingLinkDTO meetingLinkDTO) throws Exception {
        return new ResponseEntity<>(meetingService.generateMeetLink(meetingLinkDTO), HttpStatus.OK);

    }
}
