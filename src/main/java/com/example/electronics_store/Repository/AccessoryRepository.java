package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.Accessory;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long> {

    Optional<Accessory> findBySlug(String slug);

    List<Accessory> findByStatusTrueOrderByIdDesc();

    List<Accessory> findByAccessoryTypeIgnoreCaseAndStatusTrue(String accessoryType);
}