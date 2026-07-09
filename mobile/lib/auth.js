import auth from '@react-native-firebase/auth';
import firestore from '@react-native-firebase/firestore';

export function signIn({email, password}) {
  return auth().signInWithEmailAndPassword(email, password);
}

export const signUp = async ({email, password}) => {
  const newReg = await auth().createUserWithEmailAndPassword(email, password);
  console.log(newReg);
  firestore().collection('users').doc(newReg.user.uid).set({
    uid: newReg.user.uid,
    email: newReg.user.email,
  });
  return newReg;
};

export function subscribeAuth(callback) {
  return auth().onAuthStateChanged(callback);
}

export function signOut() {
  return auth().signOut;
}
