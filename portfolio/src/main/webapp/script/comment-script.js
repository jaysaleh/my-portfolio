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

/** Spaces out comment text from author name */
const commentHyphen = ' -';

/**
 * Supported class states for showing and hiding containers.
 * @enum {string}
 */
const states = {
  SHOW: 'block',
  HIDE: 'none',
}

getUserLoginData();
getSetBlobURL();

/**
 * Fetches user login data from servlet and adjusts comments section of portfolio
 * to hide comments if user is logged out.
 */
async function getUserLoginData() {
  const response = await fetch('/user-login');
  const userData = await response.json();

  var loginButtonContainer = document.getElementById('login-button-container');
  var commentForm = document.getElementById('comment-form');
  var loginButtonForm = document.getElementById('login-button-form');
  var logoutButtonForm = document.getElementById('logout-button-form');
  var lineBreak = document.getElementById('line');
  var deleteButton = document.getElementById('delete-button');

  /** Hide/show containers depending on user login state */
  if(userData.loggedIn) {
    loginButtonContainer.style.display = states.HIDE;
    commentForm.style.display = states.SHOW;
    deleteButton.style.display = states.SHOW;
    lineBreak.style.display = states.SHOW;
    
    /** Sets the logout link */
    logoutButtonForm.action = userData.logoutURL;
  } else {
    loginButtonContainer.style.display = states.SHOW;
    commentForm.style.display = states.HIDE;
    deleteButton.style.display = states.HIDE;
    lineBreak.style.display = states.HIDE;

    /** Sets the login link */
    loginButtonForm.action = userData.loginURL;
  }
}

/**
 * Fetches data from servlet and sets it in the comments section of portfolio.
 * Called whenever comments section is loaded.
 */
async function getData() {
  const response = await fetch('/data');
  const jsonData = await response.json();
  
  const commentsListElement = document.getElementById('comments-container');
  commentsListElement.innerHTML = '';
  for (comment of jsonData) {
    if(comment.commentText != '' && comment.name != '') {
      commentsListElement.appendChild(createDivElement(comment.commentText, comment.email, comment.timeStamp, comment.imageURL));
      commentsListElement.appendChild(document.createElement('br'));
    }
  }
}

/**
 * Forwards POST request to delete-data servlet and refreshes portfolio with
 * updated comments.
 */
async function deleteData() {
  const request = new Request('/delete-data', {method: 'POST'});
  const response = await fetch(request);
  getData();
}

/**
 * Fetches image upload URL from blobstore-upload-url servlet and sets
 * URL as action in {@code comment-image-form}.
 */
async function getSetBlobURL() {
  const response = await fetch('/blobstore-upload-url');
  const blobUploadURL = await response.text();
  var commentImageForm = document.getElementById("comment-image-form");
  commentImageForm.action = blobUploadURL;
}

function createDivElement(text, email, timeStamp, imageURL) {
  const outerDiv = document.createElement('div');
  const imageCommentDiv = document.createElement('div');
  const hr = document.createElement('hr');

  imageCommentDiv.id = 'img-comment-div';
  hr.id = 'line';

  imageCommentDiv.append(createImageDiv(imageURL));
  imageCommentDiv.append(createCommentDiv(text, email, timeStamp));
  
  outerDiv.append(imageCommentDiv);
  outerDiv.append(hr);

  return outerDiv;
}

function createImageDiv(imageURL) {
  const imageDiv = document.createElement('div');
  const image = document.createElement('img');
  imageDiv.id = 'img-div';
  image.id = 'img';
  image.src = imageURL;
  imageDiv.append(image);
  return imageDiv;
}

function createCommentDiv(text, email, timeStamp) {
  const commentDiv = document.createElement('div');
  const listElement = document.createElement('div');

  const textElement = document.createElement('p');
  const emailElement = document.createElement('h4');
  const dateElement = document.createElement('h5');

  commentDiv.id = 'comment-div'
  listElement.id = 'list-element';
  
  var date = new Date(timeStamp);
  var formattedDate = date.getMonth() + '/' + date.getDay() + '/' + date.getFullYear();

  textElement.innerText = text;
  emailElement.innerText = email;
  dateElement.innerText = formattedDate;

  listElement.appendChild(textElement);
  listElement.appendChild(emailElement);
  listElement.appendChild(dateElement);

  commentDiv.appendChild(listElement);
  return commentDiv;
}