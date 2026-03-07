import {h} from "@stencil/core";

import { Component, State, Listen } from '@stencil/core';

@Component({
  tag: 'analog-clock',
})
export class AnalogClock {
  timer: number;

  @State() time: number = Date.now();
  @State() timeZone: number = 0;
  @Listen('timeZoneChanged')
  timeZoneChangedHandler(event: CustomEvent) {
    this.timeZone = event.detail;
  }


  componentDidLoad() {
    this.timer = window.setInterval(() => {
      this.time = Date.now();
    }, 250);
  }

  componentDidUnload() {
    clearInterval(this.timer);
  }

  get hour(): number {
    return new Date(this.time).getHours();
  }

  get minute(): number {
    return new Date(this.time).getMinutes();
  }

  get second(): number {
    return new Date(this.time).getSeconds();
  }

  //         <time-zone-slider offset={this.timeZone}/>
  render() {
    return (
      <div>
        <clock-face hour={this.hour + this.timeZone} minute={this.minute} second={this.second}/>
        <ion-button name="test">Test</ion-button>
      </div>
    );
  }
}
