import firestore from '@react-native-firebase/firestore';

export const usersCollection = firestore().collection('users');

export function createUser({id, displayName}) {
  return usersCollection.doc(id).set({
    id,
    displayName,
  });
}

export async function getUser(id) {
  const doc = await usersCollection.doc(id).get();
  console.log(doc.data());
  return doc.data();
}
