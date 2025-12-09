package com.example.RideS.controller;

import com.example.RideS.dto.RideResponse;
import com.example.RideS.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    // View all pending rides (status = REQUESTED)
    @GetMapping("/rides/requests")
    public ResponseEntity<List<RideResponse>> getPendingRides(Authentication authentication) {
        String username = authentication.getName();
        List<RideResponse> rides = driverService.getPendingRides(username);
        return ResponseEntity.ok(rides);
    }

    // Accept a ride
    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(@PathVariable String rideId,
                                                   Authentication authentication) {
        String username = authentication.getName();
        RideResponse ride = driverService.acceptRide(rideId, username);
        return ResponseEntity.ok(ride);
    }
}
