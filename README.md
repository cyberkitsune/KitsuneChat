KitsuneChat
===========

(Quick Download: [stable build](https://github.com/downloads/cyberkitsune/KitsuneChat/KitsuneChat.jar) or [unstable build](https://dl.dropbox.com/u/27784707/KitsuneChat.jar))

Making chat RP-Safe and Customizable for all!
---------------------------------------------

KitsuneChat is a chat system replacement for Bukkit, a modified Minecraft server.
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

