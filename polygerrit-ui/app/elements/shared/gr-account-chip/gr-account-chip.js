// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

(function() {
  'use strict';

  Polymer({
    is: 'gr-account-chip',

    properties: {
      account: Object,
      removable: {
        type: Boolean,
        value: false,
      },
      showAvatar: {
        type: Boolean,
        reflectToAttribute: true,
      },
    },

    ready: function() {
      this._getHasAvatars().then(function(hasAvatars) {
        this.showAvatar = hasAvatars;
      }.bind(this));
    },

    _handleRemoveTap: function(e) {
      e.preventDefault();
      this.fire('remove', {account: this.account});
    },

    _getHasAvatars: function() {
      return this.$.restAPI.getConfig().then(function(cfg) {
        return Promise.resolve(!!(cfg && cfg.plugin && cfg.plugin.has_avatars));
      });
    },
  });
})();
