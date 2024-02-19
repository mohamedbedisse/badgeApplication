import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageMain {

    public static boolean isAvatar(BufferedImage image, int minX, int minY, int maxX, int maxY) {
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;
        int radius = (Math.max(maxX - minX, maxY - minY)) / 2; // Assuming circle is centered

        // Check if the circle's boundaries are within the image dimensions
        if (centerX - radius < 0 || centerY - radius < 0 || centerX + radius >= image.getWidth() || centerY + radius >= image.getHeight()) {
            System.out.println("Circle extends beyond image boundaries");
            return false; // Circle extends beyond image boundaries
        }

        // Iterate over the circle's boundary using Bresenham's algorithm
        for (int x = 0; x <= radius; x++) {
            int y = (int) Math.round(Math.sqrt(radius * radius - x * x));
            int pixelX = centerX + x;
            int pixelY = centerY + y;

            if (pixelX >= 0 && pixelY >= 0 && pixelX < image.getWidth() && pixelY < image.getHeight()) {
                int pixelColor = image.getRGB(pixelX, pixelY);
                int alpha = (pixelColor >> 24) & 0xFF; // Extract alpha value
                if (alpha == 0) { // Transparent pixel
                    return false;
                }
            } else {
                System.out.println("Circle extends beyond image boundaries");
                return false; // Circle extends beyond image boundaries
            }

            // Mirror the current point across all eight octants...
        }

        // Check if the contour of the circle contains only non-transparent pixels
        for (int x = minX; x <= maxX; x++) {
            int topPixel = image.getRGB(x, minY);
            int bottomPixel = image.getRGB(x, maxY);
            int topAlpha = (topPixel >> 24) & 0xFF; // Extract alpha value of top pixel
            int bottomAlpha = (bottomPixel >> 24) & 0xFF; // Extract alpha value of bottom pixel

            if (topAlpha == 0 || bottomAlpha == 0) {
                System.out.println("Contour contains transparent pixels");
                return false; // Contour contains transparent pixels
            }
        }

        for (int y = minY; y <= maxY; y++) {
            int leftPixel = image.getRGB(minX, y);
            int rightPixel = image.getRGB(maxX, y);
            int leftAlpha = (leftPixel >> 24) & 0xFF; // Extract alpha value of left pixel
            int rightAlpha = (rightPixel >> 24) & 0xFF; // Extract alpha value of right pixel

            if (leftAlpha == 0 || rightAlpha == 0) {
                System.out.println("Contour contains transparent pixels");
                return false; // Contour contains transparent pixels
            }
        }

        return true; // Avatar is inside a circle with non-transparent contour
    }
    



    // Function to check if a pixel's color falls within any of the specified sad color ranges
    public static boolean isSadPixel(int red, int green, int blue) {
    	// Define the sad color ranges
int[][] SAD_COLOR_RANGES = {
    {0, 108, 0, 108, 0, 255},       // Dark Blue
    {0, 0, 0, 192, 192, 255},       // All Nuances of Blue
    {0, 0, 64, 128, 64, 128},       // Dark Grayish Blue
    {0, 0, 128, 192, 128, 192},     // Medium Grayish Blue
    {0, 0, 128, 255, 128, 255},     // Dark Purple to Medium Purple
    
    {32, 160, 0, 96, 0, 96},        // Extended Dark Brown
    {0, 128, 0, 64, 0, 64},         // Dark Brown
    {64, 192, 0, 128, 0, 128},      // Medium Brown

    {0, 64, 0, 64, 0, 64},          // Black
    {0, 255, 0, 83, 0, 83},         // Dark Red
    {128, 255, 0, 128, 0, 128},     // Medium Red to Dark Red
    {64, 192, 0, 128, 0, 128}       // Medium Red (Extended)
};
        for (int[] range : SAD_COLOR_RANGES) {
            if (red >= range[0] && red <= range[1] &&
                green >= range[2] && green <= range[3] &&
                blue >= range[4] && blue <= range[5]) {
                return true; // Pixel color falls within this range
            }
        }
        return false; // Pixel color does not match any sad color range
    }
    
    
public static boolean isSadBadge(BufferedImage image, int minX, int minY, int maxX, int maxY) {
        int countSadPixels = 0;
        int countPixels = 0;

        // Iterate over the pixels within the circle
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Get the RGB components of the current pixel
                int pixelColor = image.getRGB(x, y);
                int red = (pixelColor >> 16) & 0xFF; // Extract red component
                int green = (pixelColor >> 8) & 0xFF; // Extract green component
                int blue = pixelColor & 0xFF; // Extract blue component

                // Check if the pixel color falls within any of the sad color ranges
                if (isSadPixel(red, green, blue)) {
                    countSadPixels++;
                }
                countPixels++;
            }
        }

        // Calculate the proportion of sad pixels
        double proportionSadPixels = (double) countSadPixels / countPixels;

        // Return true if the proportion of sad pixels is greater than 0.5
        return proportionSadPixels > 0.5;
    }

    // Main method to test the functionalities
    public static void main(String[] args) {
        // Path to the image file
        File file = new File("/home/user/Téléchargements/Avatar.jpg"); // Change this to the path of your image file

        try {
            // Read the image file into a BufferedImage object
            BufferedImage image = ImageIO.read(file);

            // Check if the image was successfully loaded
            if (image != null) {
                // Display some information about the image
                int width = image.getWidth();
                int height = image.getHeight();

                System.out.println("Image dimensions: " + width + " x " + height);

                if (width == 512 && height == 512) {
                    int minX = Integer.MAX_VALUE;
                    int minY = Integer.MAX_VALUE;
                    int maxX = Integer.MIN_VALUE;
                    int maxY = Integer.MIN_VALUE;

                    // Find the bounding box of non-transparent pixels
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            if (image.getRGB(x, y) != 0) { // Non-transparent pixel
                                minX = Math.min(minX, x);
                                minY = Math.min(minY, y);
                                maxX = Math.max(maxX, x);
                                maxY = Math.max(maxY, y);
                            }
                        }
                    }

                    if (isAvatar(image, minX, minY, maxX, maxY)) {
                        System.out.println("The image is an avatar.");

                        if (isSadBadge(image, minX, minY, maxX, maxY)) {
                            System.out.println("The avatar tends to give a sad feeling.");
                        } else {
                            System.out.println("The avatar tends to give a happy feeling.");
                        }
                    } else {
                        System.out.println("The image is not an avatar.");
                    }
                } else {
                    System.out.println("Wrong Image dimensions!");
                }
            } else {
                System.out.println("Failed to load the image.");
            }
        } catch (IOException e) {
            // Handle any IO exceptions (e.g., file not found)
            e.printStackTrace();
        }
    }
}

