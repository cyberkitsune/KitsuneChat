KitsuneChat
===========
[![Build Status](https://travis-ci.org/cyberkitsune/KitsuneChat.svg?branch=master)](https://travis-ci.org/cyberkitsune/KitsuneChat)

**[Find latest release here](https://github.com/cyberkitsune/KitsuneChat/releases)**

Making chat RP-Safe and Customizable for all!
---------------------------------------------

KitsuneChat is a chat system replacement for Spigot, a modified Minecraft server.
It splits chat into 4 "channels" and allows for user made "parties" to be used.

(KitsuneChat optionally needs the Vault plugin to be installed for extra functionality.)

Users talk on these channels by setting one as default then speaking normally,
however if they wish to quickly send a message to another channel, they jusy need to prefix their message with
the prefix given for the target channel.

Simple! :)

KitsuneChat Features:

* Chat channels: Local (block radius) Global, World Only, and Admin chat.
* User made chat parties.
* Ability to invite other users to your party!
* Permissions to force users to chat with prefixes for global, world, and party chat rather than just setting default.
* Vault integration for permission plugin based username prefixes or suffixes. 
* Fully customizable chat formats!
* Compatible with other chat plugins. (for global chat only)

KitsuneChat Permissions:

* kitsunechat.adminchat - ops get by default; Allows a user/group to talk in admin chat.
* kitsunechat.nodefault.\<channel\> - nobody gets by default; Forces the user/group who has the node to talk in the specified channel by prefix ONLY (denies /kc <channel>)

(Where \<channel\> is global, world, or party.)

KitsuneChat commands all begin with /kc. Use "/kc ?" for a help list.

Building KitsuneChat
--------------------

From command line:

1.	Download Maven
2.	Clone our repo
3.	Run `mvn clean package` on the command prompt
4.	KitsuneChat will be built in the target folder.

From Eclipse:

1.	Download the Maven Eclipse plugin (m2e)
2.	Run the saved build configuration
3.	Plugin will be generated in the target folder.
