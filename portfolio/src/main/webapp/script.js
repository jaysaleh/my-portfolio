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

/** @param slideIndex slide currently being displayed */
var slideIndex = 1;

/**
 * Supported class states for dots and slides.
 * @enum {string}
 */
const states = {
  SHOW_DOT: ' active',
  HIDE_DOT: '',
  SHOW_SLIDE: 'block',
  HIDE_SLIDE: 'none',
}

showSlides(slideIndex);

/**
 * Function computes which slide should be shown and class showSlides on that
 * specific slide. Function is called when either arrows are pressed.
 * @param {integer} n where n = 1 to go forward or n = -1 to go back
 */
function plusSlides(n) {
  showSlides(slideIndex += n);
}

/**
 * Function calls showSlides on the slide that corresponds to the dot pressed.
 * @param {integer} n where n is the number of the dot that is pressed
 */
function currentSlide(n) {
  showSlides(slideIndex = n);
}

/**
 * Function computes where the slide index is out of bounds and adjusts accordingly.
 * Function then sets all slides and dots to be not active and sets respective slide
 * and dot as active.
 * @param {integer} n where n is the slide to be displayed
 */
function showSlides(n) {
  var slides = document.getElementsByClassName("mySlides"); /** Array<div> containing all slides in slideshow-container */
  var dots = document.getElementsByClassName("dot"); /** Array<span> containing all dots in dot-container */

  // Wrap around if slideIndex is out of bounds
  if (n < 1) {
    slideIndex = slides.length;
  }
  
  if (n > slides.length) {
    slideIndex = 1;
  }
  
  // Sets style of all slides to not display
  for (var slide of slides) {
    slide.style.display = states.HIDE_SLIDE;
  }

  // Modifies className of each dot such that no dots are highlighted
  for (var dot of dots){
    dot.className = dot.className.replace(states.SHOW_DOT, states.HIDE_DOT);
  }
  
  // Activates slide and dot
  slides[slideIndex-1].style.display = states.SHOW_SLIDE;
  dots[slideIndex-1].className += states.SHOW_DOT;
}

