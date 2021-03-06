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

let map;

function createMap() {
  const homeCoords = { lat: 26.0321, lng: -80.2627 };
  map = new google.maps.Map(
    document.getElementById('map'),
    { center: homeCoords, zoom: 16,
    styles: [
            {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
            {
              featureType: 'administrative.locality',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'geometry',
              stylers: [{color: '#263c3f'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'labels.text.fill',
              stylers: [{color: '#6b9a76'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry',
              stylers: [{color: '#38414e'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry.stroke',
              stylers: [{color: '#212a37'}]
            },
            {
              featureType: 'road',
              elementType: 'labels.text.fill',
              stylers: [{color: '#9ca5b3'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry',
              stylers: [{color: '#746855'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{color: '#1f2835'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'labels.text.fill',
              stylers: [{color: '#f3d19c'}]
            },
            {
              featureType: 'transit',
              elementType: 'geometry',
              stylers: [{color: '#2f3948'}]
            },
            {
              featureType: 'transit.station',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{color: '#17263c'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.fill',
              stylers: [{color: '#515c6d'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.stroke',
              stylers: [{color: '#17263c'}]
            }
          ] 
    });
  map.addListener("click", event => {
    createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
  });

  fetchMarkers();
}

function fetchMarkers() {
  fetch('/markers').then(response => response.json()).then(markers => {
    markers.forEach(marker => {
      createMarkerForDisplay(marker.latitude, marker.longitude, marker.content);
    });
  });
}

function postMarker(latitude, longitude, content) {
  const params = new URLSearchParams();
  params.append('latitude', latitude);
  params.append('longitude', longitude);
  params.append('content', content);
  fetch('/markers', {method: 'POST', body: params});
}

function createMarkerForDisplay(latitude, longitude, content) {
  const marker =
      new google.maps.Marker({position: {lat: latitude, lng: longitude}, map: map});
  const infoWindow = new google.maps.InfoWindow({content: content});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}

let editMarker;
function createMarkerForEdit(latitude, longitude, content) {
  if (editMarker) {
    editMarker.setMap(null);
  } 
  editMarker = 
      new google.maps.Marker({position: {lat: latitude, lng: longitude}, map: map});
  
  const infoWindow =
      new google.maps.InfoWindow({content: buildInfoWindowInput(latitude, longitude)});

  google.maps.event.addListener(infoWindow, 'closeclick', () => {
    editMarker.setMap(null);
  });

  infoWindow.open(map, editMarker);
}

function buildInfoWindowInput(latitude, longitude) {
  const textBox = document.createElement('textarea');
  const button = document.createElement('button');
  button.appendChild(document.createTextNode('Submit'));

  button.onclick = () => {
    postMarker(latitude, longitude, textBox.value);
    createMarkerForDisplay(latitude, longitude, textBox.value);
    editMarker.setMap(null);
  };

  const containerDiv = document.createElement('div');
  containerDiv.appendChild(textBox);
  containerDiv.appendChild(document.createElement('br'));
  containerDiv.appendChild(button);

  return containerDiv;
}
