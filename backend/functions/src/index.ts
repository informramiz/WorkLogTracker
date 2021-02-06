import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import { CallableContext } from "firebase-functions/lib/providers/https";

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

admin.initializeApp();

const UserRoles = {regular: "regular", manager: "manager", admin: "admin"};

class ClientError extends Error {
  throwIt() {
    throw new functions.https.HttpsError("failed-precondition", this.message);
  }
}

class InvalidArgumentError extends ClientError { 
  throwIt() {
    throw new functions.https.HttpsError("invalid-argument", this.message);
  }
}

class UnAuthenticatedError extends ClientError {
  constructor() {
    super("Authentication required to perform this operationd");
  }

  throwIt() {
    throw new functions.https.HttpsError("unauthenticated", this.message);
  }
}

class InvalidRoleError extends ClientError { }

class AlreadyExistsError extends ClientError {
  throwIt() {
    throw new functions.https.HttpsError("already-exists", this.message);
  }
}



export const helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send({"result": "Hello hello...from Firebase!"});
});


exports.processSignUp = functions.auth.user().onCreate((user) => {
  // Check if user meets role criteria.
  const customClaims = {role: UserRoles.regular};
  // Set custom user claims on this newly created user.
  return admin.auth().setCustomUserClaims(user.uid, customClaims)
      .catch((error) => {
        throw new functions.https.HttpsError("internal", error.message);
      });
});

function isStringValueNotValid(value: any): Boolean {
  return !value || (value as String).length == 0;
}

function validateUserRole(callerRole: string, affectedUserRole: string) {
  if (callerRole == UserRoles.regular) {
    throw new InvalidRoleError("You do not have enough permissions to perform this operation");
  } else if (affectedUserRole == UserRoles.admin && callerRole != UserRoles.admin) {
    throw new InvalidRoleError("Only an admin can perform this operation");
  }
}

function validateUserData(data: any, context: functions.https.CallableContext) {
  if (isStringValueNotValid(data.name)) {
    throw new InvalidArgumentError("User name is required");
  }

  if (isStringValueNotValid(data.email)) {
    throw new InvalidArgumentError("User email is required");
  }

  if (isStringValueNotValid(data.password)) {
    throw new InvalidArgumentError("User password is required");
  }

  if (isStringValueNotValid(data.role)) {
    throw new InvalidArgumentError("User role is required");
  }
}

async function doesUserExist(email: string): Promise<Boolean> {
  try {
    const userRecord = await admin.auth().getUserByEmail(email);
    return userRecord.email != null;
  } catch (e) {
    
  }
  return false;
}

export const createUser = functions.https.onCall(async (data, context) => {
  try {
    if (!context.auth) {
      throw new UnAuthenticatedError();
    }

    validateUserData(data, context);
    validateUserRole(context.auth?.token.role, data.role);

    const userExists = await doesUserExist(data.email);
    if (userExists) {
      throw new AlreadyExistsError("A user with this email id already exists");
    }

    const newUser = {
      email: data.email,
      emailVerified: false,
      password: data.password,
      displayName: data.name,
      disabled: false
    };
    
    const newUserRecord = await admin.auth().createUser(newUser);
    const claims = { role: data.role };
    await admin.auth().setCustomUserClaims(newUserRecord.uid, claims);

    return { result: "New user created successfully" };
  } catch (e) {
    if (e instanceof ClientError) {
      e.throwIt();
    } else {
      throw new functions.https.HttpsError("unknown", e.message);
    }
    return {error: "User creation failed"};
  }
});

export const getUsers = functions.https.onCall(async (data, context) => {
  try {
    if (!context.auth) {
      throw new UnAuthenticatedError();
    }

    const callerRole = context.auth.token.role;
    if (!callerRole || callerRole == UserRoles.regular) {
      throw new InvalidRoleError();
    }

    const allUsers = await (await admin.auth().listUsers()).users;
    const mappedUsers = allUsers.map(mapToUserObject);
    var filteredUsers = mappedUsers.filter(item => item.userId != context.auth?.uid);
    if (callerRole != UserRoles.admin) {
      filteredUsers = filteredUsers.filter(item => item.role != UserRoles.admin);
    }

    return JSON.stringify({users:  filteredUsers});
  } catch (e) {
    if (e instanceof ClientError) {
      e.throwIt();
    } else {
      throw new functions.https.HttpsError("unknown", e.message);
    }
    
    return {error: "Getting users failed"};
  }
});

function mapToUserObject(user: admin.auth.UserRecord) {
  const userRole = user.customClaims?.role || UserRoles.regular
  return {
    name: user.displayName,
    email: user.email,
    userId: user.uid,
    role: userRole
  }
}

export const getUser = functions.https.onCall(async (data, context) => {
  try {
    if (!context.auth) {
      throw new UnAuthenticatedError();
    }
  
    const callerRole = context.auth.token.role;
    if (!callerRole || callerRole == UserRoles.regular) {
      throw new InvalidRoleError();
    }
  
    if (isStringValueNotValid(data.userId)) {
      throw new InvalidArgumentError("UserId is required");
    }
  
    const userRecord = await admin.auth().getUser(data.userId);
    const userObject = mapToUserObject(userRecord);
    if (userObject.role == UserRoles.admin && callerRole == UserRoles.manager) {
      throw new InvalidRoleError();
    }

    return JSON.stringify(userObject)
  } catch (e) {
    if (e instanceof ClientError) {
      e.throwIt();
    } else {
      throw new functions.https.HttpsError("unknown", e.message);
    }
  }

  return { error: "Unable to get userInfo" };
});

function validateUpdateUserData(data: any, context: functions.https.CallableContext) {
  if (isStringValueNotValid(data.userId)) {
    throw new InvalidArgumentError("UserId is required");
  }

  if (isStringValueNotValid(data.name)) {
    throw new InvalidArgumentError("User name is required");
  }

  if (isStringValueNotValid(data.email)) {
    throw new InvalidArgumentError("User email is required");
  }

  if (isStringValueNotValid(data.role)) {
    throw new InvalidArgumentError("User role is required");
  }
}

export const updateUser = functions.https.onCall(async (data, context) => {
  try {
    if (!context.auth) {
      throw new UnAuthenticatedError();
    }

    validateUpdateUserData(data, context);
  
    const callerRole = context.auth.token.role;
    const isCallerOwnerOfThisData = context.auth.uid == data.userId;

    if (!isCallerOwnerOfThisData) {
      validateUserRole(callerRole, data.role);
    }

    const updates = {
      email: data.email,
      displayName: data.name
    };
    await admin.auth().updateUser(data.userId, updates);

    if (!isCallerOwnerOfThisData) {
      const claims = { role: data.role };
      await admin.auth().setCustomUserClaims(data.userId, claims);
    }

    return { result: "User update successfully" };
  } catch (e) {
    if (e instanceof ClientError) {
      e.throwIt();
    } else {
      throw new functions.https.HttpsError("unknown", e.message);
    }

    return { error: "User update details failed" };
  }
});

export const deleteUser = functions.https.onCall(async (data, context) => {
  try {
    if (!context.auth) {
      throw new UnAuthenticatedError();
    }

    const callerRole = context.auth.token.role;
    const isCallerOwnerOfThisData = context.auth.uid == data.userId;

    if (!isCallerOwnerOfThisData) {
      validateUserRole(callerRole, data.role);
    }

    if (isStringValueNotValid(data.userId)) {
      throw new InvalidArgumentError("UserId is required")
    }

    await admin.auth().deleteUser(data.userId)
    return { result: "User deleted successfully" };
  } catch (e) {
    if (e instanceof ClientError) {
      e.throwIt();
    } else {
      throw new functions.https.HttpsError("unknown", e.message);
    }

    return { error: "User update details failed" };
  }
})

function validateAuthorization(context: CallableContext) {
  if (!context.auth) {
    throw new UnAuthenticatedError();
  }
}

function handleError(e: Error) {
  if (e instanceof ClientError) {
    e.throwIt();
  } else {
    throw new functions.https.HttpsError("unknown", e.message);
  }

  return { error: "User update details failed" };
}

function validateUserDataForWorkLog(data: any) {
  if (isStringValueNotValid(data.userId)) {
    throw new InvalidArgumentError("Field userId is required")
  }

  if (isStringValueNotValid(data.workLogId)) {
    throw new InvalidArgumentError("Field workLogId is required")
  }
}

function validateUserRoleForWorkLogs(context: CallableContext) {
  if (context.auth?.token.role != UserRoles.admin) {
    throw new InvalidRoleError()
  }
}

function workLogPath(userId: string, workLogId: string): string {
  return "users/" + userId + "/workLogs/" + workLogId
}

function workLogsPath(userId: string): string {
  return "users/" + userId + "/workLogs"
}

export const getWorkLog = functions.https.onCall(async (data, context) => {
  try {
    validateAuthorization(context)
    validateUserDataForWorkLog(data)
    
    const userId = data.userId
    const workLogId = data.workLogId

    validateUserRoleForWorkLogs(context)
    
    const snapshot = await admin.database().ref(workLogPath(userId, workLogId)).get()
    const value = await snapshot.val()
    return JSON.stringify(value)
  } catch (e) {
    return handleError(e)
  }
});

export const getWorkLogs = functions.https.onCall(async (data, context) => {
  try {
    validateAuthorization(context);

    if (isStringValueNotValid(data.userId)) {
      throw new InvalidArgumentError("Field userId is required");
    }
    
    validateUserRoleForWorkLogs(context);
    
    const userId = data.userId;
    
    const snapshot = await admin.database().ref(workLogsPath(userId)).limitToFirst(1000).get();
    var value = await snapshot.val();
    if (!value) {
      value = {};
    }
    return JSON.stringify(value);
  } catch (e) {
    return handleError(e);
  }
});


export const updateWorkLog = functions.https.onCall(async (data, context) => {
  try {
    validateAuthorization(context);
    validateUserDataForWorkLog(data);
    
    const userId = data.userId;
    const workLogId = data.workLogId;
    const tasksText = data.tasksText;
    const hoursWorked = data.hoursWorked;
    const date = data.date;

    if (isStringValueNotValid(tasksText)) {
      throw new InvalidArgumentError("Field tasksText is required");
    }

    if (!hoursWorked) {
      throw new InvalidArgumentError("Field hours worked is required");
    }

    if (!date) {
      throw new InvalidArgumentError("Field date is required");
    }

    validateUserRoleForWorkLogs(context);
    
    const workLogData = {
      workLogId: workLogId,
      tasksText: tasksText,
      hoursWorked: hoursWorked,
      date: date
    };
    await admin.database().ref(workLogPath(userId, workLogId)).set(workLogData);
    return JSON.stringify({ result: "Work Log updated successfully" });
  } catch (e) {
    return handleError(e)
  }
});

export const deleteWorkLog = functions.https.onCall(async (data, context) => {
  try {
    validateAuthorization(context)
    validateUserDataForWorkLog(data)
    
    const userId = data.userId
    const workLogId = data.workLogId

    validateUserRoleForWorkLogs(context)
    
    await admin.database().ref(workLogPath(userId, workLogId)).set(null)
    return JSON.stringify({ result: "Work Log deleted successfully" });
  } catch (e) {
    return handleError(e)
  }
});


export const createWorkLog = functions.https.onCall(async (data, context) => {
  try {
    validateAuthorization(context);
    if (isStringValueNotValid(data.userId)) {
      throw new InvalidArgumentError("Field userId is required")
    }
    
    const userId = data.userId;
  
    const tasksText = data.tasksText;
    const hoursWorked = data.hoursWorked;
    const date = data.date;

    if (isStringValueNotValid(tasksText)) {
      throw new InvalidArgumentError("Field tasksText is required");
    }

    if (!hoursWorked) {
      throw new InvalidArgumentError("Field hours worked is required");
    }

    if (!date) {
      throw new InvalidArgumentError("Field date is required");
    }

    validateUserRoleForWorkLogs(context);

    const child = await admin.database().ref(workLogsPath(userId)).push();
    const workLogData = {
      workLogId: child.key,
      tasksText: tasksText,
      hoursWorked: hoursWorked,
      date: date
    };
    await child.set(workLogData)

    return JSON.stringify({ result: "Work Log Created successfully" });
  } catch (e) {
    return handleError(e)
  }
});

function preferredWorkingHoursPath(userId: string): string {
  return "users/" + userId + "/preferredWorkingHoursPerDay";
}

export const getPreferredWorkingHours = functions.https.onCall(async (data, context) => {
  try {
    validateAuthorization(context);
    if (isStringValueNotValid(data.userId)) {
      throw new InvalidArgumentError("Field userId is required")
    }
    
    const userId = data.userId;
    validateUserRoleForWorkLogs(context);

    const hours = await (await admin.database().ref(preferredWorkingHoursPath(userId)).get()).val()
    return JSON.stringify({ preferredWorkingHours: hours });
  } catch (e) {
    return handleError(e)
  }
});