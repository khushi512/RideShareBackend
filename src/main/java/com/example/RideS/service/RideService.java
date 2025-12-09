package com.example.RideS.service;

import com.example.RideS.dto.CreateRideRequest;
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
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new ride (Passenger)
    public RideResponse createRide(CreateRideRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getRole().equals("ROLE_USER")) {
            throw new BadRequestException("Only ROLE_USER can create rides");
        }

        Ride ride = new Ride();
        ride.setUserId(user.getId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setStatus("REQUESTED");

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    // Complete ride (Passenger or Driver)
    public RideResponse completeRide(String rideId, String username) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new BadRequestException("Only ACCEPTED rides can be completed");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Passenger can complete only their ride, driver only their accepted ride
        if (!user.getRole().equals("ROLE_DRIVER") && !ride.getUserId().equals(user.getId())) {
            throw new BadRequestException("You are not authorized to complete this ride");
        }

        if (user.getRole().equals("ROLE_DRIVER") && !user.getId().equals(ride.getDriverId())) {
            throw new BadRequestException("You are not the driver of this ride");
        }

        ride.setStatus("COMPLETED");
        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    // Get rides for a specific user
    public List<RideResponse> getUserRides(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Ride> rides = rideRepository.findByUserId(user.getId());
        return rides.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Helper method to map Ride to RideResponse DTO
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
