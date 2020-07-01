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

/** Spaces out comment text from author name. */
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
    commentsListElement.appendChild(createListElement(comment.commentText + commentHyphen + comment.name));
  }
}

/** 
 * Creates and returns <li> containing {@code text} from comment.
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}