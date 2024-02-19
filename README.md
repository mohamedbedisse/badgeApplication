Class implementing methods to check that : 

• The size of the image is 512x512

• The only non-transparent pixels are within a circle

• The colors of the badge give a "happy" feeling

This Java class is designed to process images, specifically focusing on identifying avatars within images and determining the emotional tone associated with them. 
The application provides functions to convert an image into a BufferedImage object, identify the extreme non-transparent points in the image, check if the 
non-transparent pixels form an avatar within a circle, and determine if the avatar gives a "happy" or "sad" feeling based on its colors.

Assumptions :

• Image Format: The application assumes that the input images are in various formats such as JPEG, PNG, etc.

• Avatar Size: Avatars are expected to have dimensions of 512x512 pixels.

• Non-Transparent Pixels: The application identifies non-transparent pixels based on their alpha channel value. Pixels with an alpha value of 0 are considered transparent.

• Type of image : RGB having a channel alpha. As there is no channel alpha in black-and-white-type images, the concept of transparency would be hard to define.

• Avatar Shape: Avatars are assumed to be circular, with non-transparent pixels forming the outline of the circle.

• Emotional Tone: The determination of whether an avatar gives a "happy" or "sad" feeling is subjective and based on predefined color ranges. These color ranges are subject to interpretation and may vary for different individuals.


Methods : 

• convertToBufferedImage(String imagePath)
This function reads an image file from the specified path and converts it into a BufferedImage object.

• cardinalCoordinates(BufferedImage image)
This function calculates the extreme non-transparent points (minX, minY, maxX, maxY) in the provided image.

• isAvatar(BufferedImage image, int minX, int minY, int maxX, int maxY)
This function determines whether the non-transparent pixels in the specified region of the image form an avatar within a circle.

• isSadPixel(int red, int green, int blue)
This function checks if a given pixel color falls within predefined ranges associated with a "sad" feeling.

• isSadBadge(BufferedImage image, int minX, int minY, int maxX, int maxY)
This function analyzes the colors of the pixels within the specified region of the image to determine if the avatar tends to give a "sad" feeling.

• main(String[] args)
The main function of the application. It processes command-line arguments to load an image, perform avatar identification and emotional tone analysis, and display the results.

Usage :

To use the application, run the ImageMain class with the path to the image file as a command-line argument:
java ImageMain <image_path>
