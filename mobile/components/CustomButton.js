import React from 'react';
import {View, StyleSheet, Platform, Text, Pressable} from 'react-native';

const CustomButton = ({onPress, title, hasMarginBottom, theme}) => {
  const isPrimary = theme === 'primary';

  return (
    <View
      style={[
        styles.block,
        styles.overflow,
        hasMarginBottom && styles.margin,
        isPrimary && styles.primaryButton,
      ]}>
      <Pressable
        onPress={onPress}
        style={({pressed}) => [
          styles.wraper,
          isPrimary && styles.primaryWrapper,
          Platform.OS === 'ios' && pressed && {opacity: 0.5},
        ]}>
        <Text
          style={[
            styles.text,
            isPrimary ? styles.primaryText : styles.secondaryText,
          ]}>
          {title}
        </Text>
      </Pressable>
    </View>
  );
};

CustomButton.defaultProps = {
  theme: 'primary',
};

const styles = StyleSheet.create({
  overflow: {
    borderRadius: 4,
    overflow: 'hidden',
  },
  margin: {
    marginBottom: 8,
  },
  primaryButton: {
    backgroundColor: '#1976D2',
  },
  wraper: {
    borderRadius: 4,
    height: 48,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontWeight: 'bold',
    fontSize: 14,
    color: 'white',
  },
  primaryWrapper: {
    backgroundColor: '#1976D2',
  },
  primaryText: {
    color: 'white',
  },
  secondaryText: {
    color: '#1976D2',
  },
});

export default CustomButton;
