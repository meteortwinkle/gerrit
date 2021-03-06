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

package com.google.gerrit.server.notedb;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gerrit.reviewdb.client.Change;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.notedb.NoteDbUpdateManager.Result;
import com.google.gerrit.server.project.NoSuchChangeException;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.SchemaFactory;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class ChangeRebuilder {
  private final SchemaFactory<ReviewDb> schemaFactory;

  protected ChangeRebuilder(SchemaFactory<ReviewDb> schemaFactory) {
    this.schemaFactory = schemaFactory;
  }

  public final ListenableFuture<Result> rebuildAsync(
      final Change.Id id, ListeningExecutorService executor) {
    return executor.submit(new Callable<Result>() {
        @Override
      public Result call() throws Exception {
        try (ReviewDb db = schemaFactory.open()) {
          return rebuild(db, id);
        }
      }
    });
  }

  public abstract Result rebuild(ReviewDb db, Change.Id changeId)
      throws NoSuchChangeException, IOException, OrmException,
      ConfigInvalidException;

  public abstract Result rebuild(NoteDbUpdateManager manager,
      ChangeBundle bundle) throws NoSuchChangeException, IOException,
      OrmException, ConfigInvalidException;

  public abstract boolean rebuildProject(ReviewDb db,
      ImmutableMultimap<Project.NameKey, Change.Id> allChanges,
      Project.NameKey project, Repository allUsersRepo)
      throws NoSuchChangeException, IOException, OrmException,
      ConfigInvalidException;

  public abstract NoteDbUpdateManager stage(ReviewDb db, Change.Id changeId)
      throws NoSuchChangeException, IOException, OrmException;

  public abstract Result execute(ReviewDb db, Change.Id changeId,
      NoteDbUpdateManager manager) throws NoSuchChangeException, OrmException,
      IOException;
}
