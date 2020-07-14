// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.util.*; 
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.Comment.Builder;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that writes messages as a response. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  // Used to create Entity and its fields.
  private static final String COMMENT = "Comment";
  private static final String TIME_STAMP = "timeStamp";
  private static final String NAME = "name";
  private static final String COMMENT_TEXT = "commentText";
  private static final String EMAIL = "email";
  private static final String IMAGE_URL = "imageUrl";

  // Supported image files.
  private static final String JPEG = "image/jpeg";
  private static final String PNG = "image/png";
  private static final String TIFF = "image/tiff";

  // Name of input field used for author name in comments section.
  private static final String NAME_INPUT = "name-input";
  // Name of input field used for comment text in comments section.
  private static final String COMMENT_INPUT = "comment-input";
  // Default value if comment section inputs are empty.
  private static final String DEFAULT_VALUE = "";
  private static final String REDIRECT_URL = "/html/comments.html";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = getParameter(request, NAME_INPUT, DEFAULT_VALUE);
    String commentText = getParameter(request, COMMENT_INPUT, DEFAULT_VALUE);
    long timeStamp = System.currentTimeMillis();

    // Creates Entity and stores in database.
    Entity commentEntity = new Entity(COMMENT);
    commentEntity.setProperty(NAME, name);
    commentEntity.setProperty(EMAIL, getEmail());
    commentEntity.setProperty(IMAGE_URL, getUploadedFileUrl(request, /* forInputElement= */ "image").get());
    commentEntity.setProperty(COMMENT_TEXT, commentText);
    commentEntity.setProperty(TIME_STAMP, timeStamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect(REDIRECT_URL);
  }

  /** 
   * Returns the URL that points to a file uploaded by {@code formInputElementName}, 
   * or an empty optional if the user didn't upload an image file. 
   */
  private Optional<String> getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form in the local server without a file, so we can't get a URL.
    if (blobKeys == null || blobKeys.isEmpty()) {
      return Optional.empty();
    }

    BlobKey blobKey = blobKeys.get(0);

    // User submitted form in the live server without a file, so we can't get a URL.
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return Optional.empty();
    }

    String fileInfo = blobInfo.getContentType();

    // Return empty optional if file is not a jpg, png or tiff image.
    if (!fileInfo.equals(JPEG) && !fileInfo.equals(PNG) && !fileInfo.equals(TIFF)) {
      return Optional.empty();
    }

    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return Optional.of(url.getPath());
    } catch (MalformedURLException e) {
      return Optional.of(imagesService.getServingUrl(options));
    }
  }
  
  /**
   * Returns email of currently logged in user.
   */
  private String getEmail() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.getCurrentUser().getEmail();
  }

  /**
   * Returns value with {@code name} from the {@code request} form. 
   * If the {@code name} cannot be found, return {@code defaultValue}.
   * @param request Form sent by client.
   * @param name {@code <input>} or {@code <textarea>} to read content of.
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(COMMENT).addSort(TIME_STAMP, SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty(NAME);
      String email = (String) entity.getProperty(EMAIL);
      String imageUrl = (String) entity.getProperty(IMAGE_URL);
      String commentText = (String) entity.getProperty(COMMENT_TEXT);
      long timeStamp = (long) entity.getProperty(TIME_STAMP);
      
      // Creates new Comment for JSON accessibility.
      Builder commentBuilder = Comment.builder(id, name, email, commentText, timeStamp);
      if (imageUrl.isEmpty()) {
        comments.add(commentBuilder.build());
        continue;
      }
      comments.add(commentBuilder.setImageUrl(Optional.of(imageUrl)).build());
    }

    Gson gson = new Gson();
    response.setContentType("text/html;");
    response.getWriter().println(gson.toJson(comments));
  }
}
