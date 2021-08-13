/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Common from '../common/common.js';
import {Dialog} from './Dialog.js';
import {SizeBehavior} from './GlassPane.js';
import {createTextButton, formatLocalized} from './UIUtils.js';
import {VBox} from './Widget.js';

export class RemoteDebuggingTerminatedScreen extends VBox {
  /**
   * @param {string} reason
   */
  constructor(reason) {
    super(true);
    this.registerRequiredCSS('ui/remoteDebuggingTerminatedScreen.css');
    const message = this.contentElement.createChild('div', 'message');
    const reasonElement = message.createChild('span', 'reason');
    reasonElement.textContent = reason;
    message.appendChild(formatLocalized('Debugging connection was closed. Reason: %s', [reasonElement]));
    this.contentElement.createChild('div', 'message').textContent =
        Common.UIString.UIString('Reconnect when ready by reopening DevTools.');
    const button = createTextButton(Common.UIString.UIString('Reconnect DevTools'), () => window.location.reload());
    this.contentElement.createChild('div', 'button').appendChild(button);
  }

  /**
   * @param {string} reason
   */
  static show(reason) {
    const dialog = new Dialog();
    dialog.setSizeBehavior(SizeBehavior.MeasureContent);
    dialog.addCloseButton();
    dialog.setDimmed(true);
    new RemoteDebuggingTerminatedScreen(reason).show(dialog.contentElement);
    dialog.show();
  }
}