-- 519030910270 秦健行

-- Rules implemented:
-- 1.1, 1.2, 1.3
-- 2.1, 2.2
-- 2.3, 2.4, 2.5, 2.6, 2.9

-- for extra operations and invariants implemented
-- see the corresponding annotated code

-- note that operations are limited so some invariants
-- may be obviously correct, but invariants also describes
-- a static model which can be generated for other purposes

sig Uid {}
sig Wechat {
	uids: set Uid,
	users: set User,
	umap: uids one -> one users,
	moments: set Moment,
	chats: set Chat
}

sig User {
	id: Uid,
	friends: set Uid,
	blacklist: set Uid,
	blocklist: set Uid  -- block moments from these users
}
sig Moment {
	from: Uid,
	blocklist: set Uid, -- block moments to these users
	comments: set Comment
}
sig Comment {
	from: Uid,
	replyTo: lone Comment
}
sig Chat {
	between: set Uid,
	history: set Message
} { #between = 2 }
sig Message { from, to: Uid }

-- for cleaner structure
fact NoOrphanUser { all u: User | one w: Wechat | u in w.users }
fact NoOrphanMoment { all m: Moment | one w: Wechat | m in w.moments }
fact NoOrphanChat { all c: Chat | one w: Wechat | c in w.chats }
fact NoOrphanComment { all c: Comment | one m: Moment | c in m.comments }
fact NoOrphanMessage { all m: Message | one c: Chat | m in c.history }

-- initial wechat is empty
pred init[w: Wechat] {
	no w.uids and no w.users and no w.umap and no w.moments and no w.chats
}

-- INVARIANTS
-- well-formed structure
pred invBasic[w: Wechat] {
	-- all references are valid
	all u: w.users |
		u.friends in w.uids and
		u.blacklist in w.uids and
		u.blocklist in w.uids
	all m: w.moments |
		m.from in w.uids and m.blocklist in w.uids
	all c: w.chats |
		c.between in w.uids
	all c: Comment, m: w.moments |
		c in m.comments implies c.replyTo in m.comments
	-- ownership, force tree structure
	all c: Comment, p, q: w.moments |
		c in p.comments and c in q.comments implies p = q
	all m: Message, p, q: w.chats |
		m in p.history and m in q.history implies p = q
}
-- reflective friend for convenient
pred invFriendRefl[w: Wechat] {
	all u: w.users | u.id in u.friends
}
pred invNoBlockSelf[w: Wechat] {
	all u: w.users |
		u.id not in u.blocklist and
		u.id not in u.blacklist
}
pred invChatOnlyFriends[w: Wechat] {
	all c: w.chats, u: w.uids |
		u in c.between implies c.between in w.umap[u].friends
}
pred invChatHistoryValid[w: Wechat] {
	all c: w.chats, m: Message |
		m in c.history implies m.from in c.between and m.to in c.between
}
pred invNoSelfMessage[w: Wechat] {
	all c: w.chats, m: Message |
		m in c.history implies not m.from = m.to
}
pred invNoCircReply[w: Wechat] {
	all m: w.moments, c: Comment |
		c in m.comments implies c not in c.^replyTo
}

pred inv[w: Wechat] {
	invBasic[w]
	invFriendRefl[w]
	invNoBlockSelf[w]
	invChatOnlyFriends[w]
	invChatHistoryValid[w]
	invNoSelfMessage[w]
	invNoCircReply[w]
}

-- OPERATIONS
-- create a new user
pred createUser[w, w1: Wechat] {
	one u: User |
		u not in w.users and
		u.id not in w.uids and
		u.friends = u.id and
		no u.blocklist and
		no u.blacklist and
		w1.uids = w.uids + u.id and
		w1.users = w.users + u and
		w1.umap = w.umap + u.id -> u and
		w1.moments = w.moments and
		w1.chats = w.chats
}

-- posting a moment with blocks
-- is separated to adding a new moment
-- and adding blocks to that moment
-- some user post a moment
pred postMoment[w, w1: Wechat] {
	one u: w.users |
	one m: Moment |
		m not in w.moments and
		m.from = u.id and
		no m.blocklist and
		no m.comments and
		w1.uids = w.uids and
		w1.users = w.users and
		w1.umap = w.umap and
		w1.moments = w.moments + m and
		w1.chats = w.chats
}
-- some user add a block to a existing moment
pred addMomentBlock[w, w1: Wechat] {
	one u: w.uids |
	one m: w.moments |
	one v: w.umap[u].friends |
	one m1: Moment |
		not u = v and
		m.from = u and
		m1.from = m.from and
		m1.blocklist = m.blocklist + v and
		m1.comments = m.comments and
		w1.uids = w.uids and
		w1.users = w.users and
		w1.umap = w.umap and
		w1.moments = w.moments - m + m1 and
		w1.chats = w.chats
}

-- some user add a friend
-- rule 2.1, 2.2
pred addFriend[w, w1: Wechat] {
	one u: w.users |
	one u1: User |
	one v: w.uids |
		u.id not in w.umap[v].blacklist and
		u1.id = u.id and
		u1.friends = u.friends + v and
		u1.blacklist = u.blacklist and
		u1.blocklist = u.blocklist and
		w1.uids = w.uids and
		w1.users = w.users - u + u1 and
		w1.umap = w.umap - (u.id -> u) + (u1.id -> u1) and
		w1.moments = w.moments and
		w1.chats = w.chats
}

-- rule 2.5, 2.6
-- send a message in existing chat
pred sendChatMessage[w, w1: Wechat] {
	one u: w.uids |
	one v: w.uids |
	one c: w.chats |
	one c1: Chat |
	one m: Message |
		u not in w.umap[v].blacklist and
		m.from = u and m.to = v and
		m not in c.history and
		u in c.between and v in c.between and
		c1.between = c.between and
		c1.history = c.history + m and
		w1.uids = w.uids and
		w1.users = w.users and
		w1.umap = w.umap and
		w1.moments = w.moments and
		w1.chats = w.chats - c + c1
}
-- send the first message
pred sendNewMessage[w, w1: Wechat] {
	one u: w.uids |
	one v: w.uids |
	one c: Chat |
	one m: Message |
		u not in w.umap[v].blacklist and
		u in w.umap[v].friends and
		v in w.umap[u].friends and
		m.from = u and m.to = v and
		c not in w.chats and
		u in c.between and v in c.between and
		c.history = m and
		w1.uids = w.uids and
		w1.users = w.users and
		w1.umap = w.umap and
		w1.moments = w.moments and
		w1.chats = w.chats + c
}

-- some user add another user to blacklist
pred addBlackList[w, w1: Wechat] {
	one u: w.users |
	one u1: User |
	one v: w.uids |
		not u.id = v and
		u1.id = u.id and
		u1.blacklist = u.blacklist + v and
		u1.friends = u.friends and
		u1.blocklist = u.blocklist and
		w1.uids = w.uids and
		w1.users = w.users - u + u1 and
		w1.umap = w.umap - (u.id -> u) + (u1.id -> u1) and
		w1.moments = w.moments and
		w1.chats = w.chats
}
pred revertBlackList[w, w1: Wechat] {
	one u: w.users |
	one u1: User |
	one v: w.uids |
		v in u.blacklist and
		u1.id = u.id and
		u1.blacklist = u.blacklist - v and
		u1.friends = u.friends and
		u1.blocklist = u.blocklist and
		w1.uids = w.uids and
		w1.users = w.users - u + u1 and
		w1.umap = w.umap - (u.id -> u) + (u1.id -> u1) and
		w1.moments = w.moments and
		w1.chats = w.chats
}

pred step[w, w1: Wechat] {
	createUser[w, w1] or
	postMoment[w, w1] or
	addMomentBlock[w, w1] or
	addFriend[w, w1] or
	sendChatMessage[w, w1] or
	sendNewMessage[w, w1] or
	addBlackList[w, w1] or
	revertBlackList[w, w1]
}

-- "visible"s are static predicates
-- (determine visibility from the state but do not change the state)
-- so they are implemented just as the according rules
-- and are not included in verification
-- rule 1.1, 2.9
pred momentVisibleTo[w: Wechat, u: w.uids, m: w.moments] {
	u in w.umap[m.from].friends and
	u not in m.blocklist and
	m.from not in w.umap[u].blocklist and
	m.from not in w.umap[u].blacklist and
	u not in w.umap[m.from].blacklist
}
-- rule 1.2, 1.3
pred comVisibleTo[w: Wechat, u: w.uids, c: Comment] {
	some m: w.moments |
		momentVisibleTo[w, u, m] and c in m.comments and
		c.replyTo.from in w.umap[u].friends
}
-- rule 2.3, 2.4
pred chatVisibleTo[w: Wechat, u: w.uids, c: w.chats] {
	u in c.between and
	(c.between - u) not in w.umap[u].blacklist
}

-- SPECIFICATION
assert Correctness {
	all w: Wechat | init[w] implies inv[w]
	all w, w1: Wechat | inv[w] and step[w, w1] implies inv[w1]
}

-- uncomment following line to see a static model
-- run inv
check Correctness
