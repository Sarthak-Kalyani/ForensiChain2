package com.sdcems.sdcems.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MetadataService {

    public String extractMetadata(File file) {

        StringBuilder result = new StringBuilder();

        try {

            Metadata metadata =
                    ImageMetadataReader.readMetadata(file);

            result.append("FORENSIC METADATA ANALYSIS\n\n");

            result.append("File Name: ")
                    .append(file.getName())
                    .append("\n");

            result.append("File Size: ")
                    .append(file.length())
                    .append(" bytes\n\n");

            boolean found = false;

            for (Directory directory : metadata.getDirectories()) {

                result.append("[")
                        .append(directory.getName())
                        .append("]\n");

                for (Tag tag : directory.getTags()) {

                    result.append(tag.getTagName())
                            .append(": ")
                            .append(tag.getDescription())
                            .append("\n");

                    found = true;
                }

                result.append("\n");
            }

            if (!found) {
                result.append(
                    "No embedded EXIF/IPTC/XMP metadata found.\n"
                );
            }

            return result.toString();

        } catch (Exception e) {

            return "Metadata extraction unavailable: "
                    + e.getMessage();
        }
    }
}