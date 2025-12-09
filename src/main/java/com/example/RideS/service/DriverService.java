package com.example.RideS.service;

import com.example.RideS.dto.RideResponse;
import com.example.RideS.exception.BadRequestException;
import com.example.RideS.exception.NotFoundException;
import com.example.RideS.model.Ride;
import com.example.RideS.model.User;
import com.example.RideS.repository.RideRepository;
import com.example.RideS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all rides with status REQUESTED
    public List<RideResponse> getPendingRides(String username) {
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        if (!driver.getRole().equals("ROLE_DRIVER")) {
            throw new BadRequestException("Only ROLE_DRIVER can view pending rides");
        }

        List<Ride> rides = rideRepository.findByStatus("REQUESTED");
        return rides.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Accept a ride
    public RideResponse acceptRide(String rideId, String username) {
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        if (!driver.getRole().equals("ROLE_DRIVER")) {
            throw new BadRequestException("Only ROLE_DRIVER can accept rides");
        }

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("REQUESTED")) {
            throw new BadRequestException("Ride is not available for acceptance");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus("ACCEPTED");
        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    // Helper to convert Ride to RideResponse
    private RideResponse mapToResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getUserId(),
                ride.getDriverId(),
                ride.getPickupLocation(),
                ride.getDropLocation(),
                ride.getStatus(),
                ride.getCreatedAt()
        );
    }
}
