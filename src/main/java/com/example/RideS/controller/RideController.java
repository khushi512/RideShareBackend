package com.example.RideS.controller;

import com.example.RideS.dto.CreateRideRequest;
import com.example.RideS.dto.RideResponse;
import com.example.RideS.service.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RideController {

    @Autowired
    private RideService rideService;

    // Create a ride (ROLE_USER)
    @PostMapping("/rides")
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request,
                                                   Authentication authentication) {
        String username = authentication.getName();
        RideResponse ride = rideService.createRide(request, username);
        return ResponseEntity.ok(ride);
    }

    // Get own rides (ROLE_USER)
    @GetMapping("/user/rides")
    public ResponseEntity<List<RideResponse>> getUserRides(Authentication authentication) {
        String username = authentication.getName();
        List<RideResponse> rides = rideService.getUserRides(username);
        return ResponseEntity.ok(rides);
    }

    // Complete ride (ROLE_USER or ROLE_DRIVER)
    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId,
                                                     Authentication authentication) {
        String username = authentication.getName();
        RideResponse ride = rideService.completeRide(rideId, username);
        return ResponseEntity.ok(ride);
    }
}
