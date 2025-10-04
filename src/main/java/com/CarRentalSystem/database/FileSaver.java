package com.CarRentalSystem.database;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class FileSaver {
    static String endpoint = "https://hkdjkhiokhaqkqzuevpr.supabase.co/storage/v1/s3";
    static String accessKey = "71b835ca561f5099e27d3a4e507f07a2"; // Replace with your Supabase access key
    static String secretKey = "1d61860eae1bf7aeeba1816b7cc911bfa578d14bfb4ec2f35698f11115806624"; // Replace with your Supabase secret key
    static String bucketName = "uploads"; // Replace with your bucket name

    // Create an S3 client
    static S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create(endpoint)) // S3-compatible endpoint
            .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .region(Region.US_EAST_1) // Region can be arbitrary for S3 compatibility
            .build();

    public static void savePassportFile(File file, UUID personId) throws SQLException {
        // Define the upload key (path in the bucket)
        String uploadKey = "passports/" + personId.toString() + "_" + file.getName();

        try {
            // Create the PutObjectRequest
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName) // Use the correct bucket name
                    .key(uploadKey)     // Path in the bucket
                    .build();

            // Upload the file
            s3Client.putObject(request, file.toPath());
            System.out.println("File uploaded successfully to S3: " + uploadKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        // Update the database with the S3 file path (uploadKey)
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE \"Person\" SET passport_path = ? WHERE id = ?")) {
            stmt.setString(1, uploadKey); // Save the relative path (key) in the database
            stmt.setObject(2, personId);
            stmt.executeUpdate();
        }
    }


    public static void saveLicienseFile(File file, UUID personId) throws IOException, SQLException {
        String uploadDir = "/uploads/licenses/";
        String fileName = personId.toString() + "_" + file.getName();
        File dest = new File(uploadDir + fileName);

        Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        String filePath = dest.getAbsolutePath();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE \"Member\" SET license_path = ? WHERE id = ?")) {
            statement.setString(1, filePath);
            statement.setObject(2, personId);
            statement.executeUpdate();
        }
    }

}
