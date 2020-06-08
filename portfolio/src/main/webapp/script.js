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

/*Pulls comments and adds html/styling elements to display*/
function getComments(maxComments) {
  const url = "/data?maxComments=" + maxComments;
  fetch(url).then(response => response.json()).then(comments => {

    const commentContainer = document.getElementById('comments-container');
    commentContainer.innerHTML = '';
    comments.forEach(comment => {
      commentContainer.appendChild(createCommentElement(comment));
    })
  });
}

function createCommentElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.id = 'comment';

  const messageElement = document.createElement('span');
  messageElement.innerHTML = comment.message;

  commentElement.appendChild(messageElement);
  return commentElement
}

function deleteComments() {
  const request = new Request('/delete-data', { method: 'POST' })
  fetch(request).then(getComments(0));
}

function createMap() {
  console.log("reached here");
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 20.0321, lng: -80.2627}, zoom: 16});
}
