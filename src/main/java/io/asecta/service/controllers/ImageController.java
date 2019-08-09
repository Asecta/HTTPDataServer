package io.asecta.service.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.Status;
import io.asecta.rest.router.autorouter.AutoRouter;
import io.asecta.rest.router.autorouter.Mapping;
import io.asecta.rest.router.requesthandler.RequestBody;
import io.asecta.rest.router.requesthandler.RequestPayload;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;

public class ImageController extends AutoRouter {

	private static final String IMAGE_DIRECTORY = "images";

	@Override
	public String getBaseRoute() {
		return "/images";
	}

	@Override
	public boolean isAcceptable(RequestPayload request) {
		return true;
	}

	@Override
	public void handleBase(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {

	}

	@Mapping("view")
	public void handleGetByToken(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {
		try {

			if (request.getRequestURI().contains("\\.\\.")) {
				payload.setMessage("Nope");
				return;
			}

			String path = pathParams[2];

			File file = null;
			String extention = null;

			for (File image : new File(IMAGE_DIRECTORY).listFiles()) {
				String[] params = image.getName().split("\\.");

				if (params[0].equalsIgnoreCase(path)) {
					file = image;
					extention = params[1];
					break;
				}
			}

			if (file == null) {
				throw new RuntimeException("File not Found");
			}

			System.out.println(path);
			byte[] data = null;

			BufferedImage image = ImageIO.read(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, extention, out);
			data = out.toByteArray();
			out.close();

			payload.setContent(data);
			payload.setImage();
			payload.setContentType("image/" + extention);
		} catch (Exception e) {
			payload.setContent("Image Not Found");
		}
	}

	@Mapping("upload")
	public void handleUpload(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {
		if (accessor.isAnonymous()) {
			payload.setStatus(Status.UNAUTHORIZED);
			return;
		}

		String blob = body.getString("data");
		String result = null;

		try {
			result = ImageController.saveFile(blob);
		} catch (Exception e) {
			payload.setStatus(Status.BAD_REQUEST);
			payload.setMessage("Couldn't Upload Image");
			e.printStackTrace();
		}

		payload.setMessage(result);
	}

	private static List<String> extentions = Arrays.asList(new String[] { "jpg", "jpeg", "png" });

	public static String saveFile(String blob) throws RuntimeException {

		String uuid = UUID.randomUUID().toString();
		System.out.println(blob.substring(0, 100));

		try {
			String extention = extractExtentionFromBase64(blob).toLowerCase();
			String base64Image = blob.split(",")[1];

			if (!extentions.contains(extention)) {
				throw new RuntimeException("File extention not allowed");
			}

			File file = null;
			while (file == null || file.exists()) {
				uuid = UUID.randomUUID().toString();
				file = new File(IMAGE_DIRECTORY, String.format("%s.%s", uuid, extention));
			}

			byte[] imageBytes = Base64.getDecoder().decode(base64Image);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

			file.getParentFile().mkdirs();

			ImageIO.write(img, extention, file);
		} catch (IOException e) {
			throw new RuntimeException("Invalid Image Format");
		}

		return uuid;
	}

	public static String extractExtentionFromBase64(String blob) {
		return blob.split(";")[0].split("/")[1];
	}

}
