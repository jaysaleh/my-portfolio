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
const commentHyphen = " -";

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
    if(comment.commentText != "" && comment.name != ""){
      commentsListElement.appendChild(createListElement(comment.commentText, comment.name, comment.timeStamp));
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
 * Creates and returns a div element containing {@code text}, {@code name},
 * and {@code timeStamp} from comment.
 */
function createListElement(text, name, timeStamp) {
  const commentDiv = document.createElement('div');
  const textElement = document.createElement('p');
  const nameElement = document.createElement('h4');
  const dateElement = document.createElement('h5');

  commentDiv.id = 'list-element';
  
  var date = new Date(timeStamp);
  var formattedDate = date.getMonth() + '/' + date.getDay() + '/' + date.getFullYear();

  textElement.innerText = text;
  nameElement.innerText = name;
  dateElement.innerText = formattedDate;

  commentDiv.appendChild(textElement);
  commentDiv.appendChild(nameElement);
  commentDiv.appendChild(dateElement);

  return commentDiv;
}