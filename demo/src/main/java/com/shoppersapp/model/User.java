package com.shoppersapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "user_id")
   private Integer userId;

   @Column(name = "first_name", nullable = false, length = 50)
   private String firstName;

   @Column(name = "last_name", nullable = false, length = 50)
   private String lastName;

   @Column(name = "email", nullable = false, unique = true, length = 255)
   private String email;

   @Column(name = "phone_number", nullable = false, unique = true, length = 20)
   private String phoneNumber;

   @Column(name = "date_of_birth", nullable = false)
   private LocalDate dateOfBirth;

   @Column(name = "address", nullable = false, length = 255)
   private String address;

   // password_hash is BYTEA, map to byte[]
   @Column(name = "password_hash", nullable = false)
   private byte[] passwordHash;

   @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
   private LocalDateTime createdAt;

   public User() {
   }

   // Constructor without userId (for inserts)
   public User(String firstName, String lastName, String email, String phoneNumber,
         LocalDate dateOfBirth, String address, byte[] passwordHash) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.phoneNumber = phoneNumber;
      this.dateOfBirth = dateOfBirth;
      this.address = address;
      this.passwordHash = passwordHash;
      this.createdAt = LocalDateTime.now();
   }

   public User(Integer userId, String firstName, String lastName, String email, String phoneNumber,
         LocalDate dateOfBirth, String address, byte[] passwordHash) {
      this.userId = userId;
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.phoneNumber = phoneNumber;
      this.dateOfBirth = dateOfBirth;
      this.address = address;
      this.passwordHash = passwordHash;
      this.createdAt = LocalDateTime.now();
   }

   public Integer getUserId() {
      return userId;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public LocalDate getDateOfBirth() {
      return dateOfBirth;
   }

   public void setDateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public byte[] getPasswordHash() {
      return passwordHash;
   }

   public void setPasswordHash(byte[] passwordHash) {
      this.passwordHash = passwordHash;
   }

   public LocalDateTime getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }
}